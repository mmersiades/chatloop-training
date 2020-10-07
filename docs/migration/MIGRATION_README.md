### Migration
#### Overview
When the microservice and its database is first established, a migration needs to be run. This migration will look at
all the existing users on the Chatloop system (namely, in the monolith's `user` table), and create rows in the training
microservice tables for these Users, with all their course and module progress set to 'complete'. This is so that 
Users on the pilot and alpha programs don't have to redo the training they did earlier.

There are quite a few steps in this migration process, which are outlined here and detailed below:

1. Get a list of user ids from the monolith.
2. Get the latest content data (programs, courses modules etc) from Contentful.
3. Open an SSH tunnel to the database (currently not needed. Temporarily).
4. Use psql to run the `migrator.sql` script.

#### Prerequisites
##### SSH
_Note: As of Oct 2020, ssh is *not* needed, but this is a temporary condition_
You'll need to be able to create a ssh tunnel with the database. You'll need to do this via the EC2 instance associate
with the Elastic Beanstalk environment. For this, you'll need a `pem` key, which can be created in the AWS console EC2
UI, under "Key Pairs" in the menu. The `pem` key should have read-only permissions, which can be set with this command:
```bash
chmod 400 [path & pem file]
```

To open the ssh tunnel, open a DIFFERENT terminal window and run the following command:

```bash
ssh -i [path & pem file] -N -L [local port]:[RDS db hostname/endpoint]:5432 ec2-user@[ec2 instance public ip]
```
Notes

* path & pem file = the path to the folder where you've saved the `pem` file, with the pem file at the end.
  * eg: `~/.ssh/staging-user-training.pem`
* local port = can be any port on your computer that's not being used.
  * eg: `15432`
* RDS db hostname = you can get this from the RDS instance in the AWS RDS console, under the "Connectivity & security" tab
  * eg: `chatloop-staging-user-training-db.cramxfgxfhjl.ap-southeast-2.rds.amazonaws.com`
* EC2 instance public ip = you can get the "IPv4 Public IP" from the AWS EC2 console for the instance, under the "Description" tab.
  * eg: `3.25.221.67`
* Complete example
  * `ssh -i ~/.ssh/staging-user-training.pem -N -L 15432:chatloop-staging-user-training.cramxfgxfhjl.ap-southeast-2.rds.amazonaws.com:5432 ec2-user@3.25.197.157`

If the tunnel is successfully created, all you'll see is a blinking cursor on the next line. The tunnel will remain open
until you close the terminal or ^c 

##### Credentials
You'll need to know:

* db name. Can be found in RDS console under "configuration" tab.
* db username. Can be found in RDS console under "configuration" tab.
* db password. You can get this from AWS' Secrets Manager if you don't have it already.
* db port. It's `5432`.

##### psql
psql is official postgres CLI client. Install it by following installation instructions for your OS.

To login use `psql --host=HOST --port=PORT --username=USER --dbname=DBNAME`.

For local, this would be: `psql --host localhost --port=5555 --username=cut --dbname=cut`. 

Notes

* HOST = "RDS db hostname" (see above)
* PORT = `5432`
* USER = "db username" (see above)
* DBNAME = `ebdb`

If the psql connection is successful, the prompt will ask you for your password, which should be `cut` for local
postgres, the "db password" (see above) for RDS databases.

#### Step 1 - User IDs
First export all user IDs from the monolith's database into a local file. Assume the file is called 'monolith-user-ids.csv'.
This can be done through command line with psql, or via the pgadmin web console with a manual download of CSV file.

See example test file with same name in test package.

Use this SQL command:

```sql
    SELECT
        users.id as user_id
    FROM
        public.users as users,
        public.user_roles as roles
    WHERE
        users.id = roles.user_id AND
        roles.role in ('ROLE_PARTNER', 'ROLE_TUTOR')
    ORDER BY
        users.id;
```
    
Save the `monolith-user-ids.csv` file in the project root folder.

#### Step 2 - Contentful training content data
Next, we need list of all unique space_id/env_id/course_id/module_id tuples from Contentful.

Use the class `CdnCrawler` to extract a CSV file with this data by connecting to real Contentful. The class is in the
project's `test` folder. To run it, un-comment the `@ignore` statement at the top of the class, and then run it like
you would run any individual test.

`CdnCrawler` will create a file in the project root folder called `contentful-data.csv`. See example test file with same
name in test package.

#### Step 3 - SSH tunnel
_Note: As of Oct 2020, ssh is *not* needed, but this is a temporary condition_
If you're trying to insert this data into an RDS database (ie, not local), you'll need to establish an SSH tunnel now.
See the instructions about.

#### Step 4 - Run migration script
If you've saved `monolith-user-ids.csv` or `contentful-data.csv` to somewhere that's NOT the project's root folder,
you'll need to modify `migrator.sql` so that it can find those two files.

In a terminal, navigate to the project root folder.

Now we are ready to import data and populate our tables.

Run psql from the project's root folder (see instructions above).

From within psql, execute command `\i ./docs/migration/migrator.sql;`.

If psql reports no errors, double check the data has been inserted into the db. You can do this directly
from your psql by writing SQL queries like `SELECT * FROM courses_progress;`. You should see a lot of rows for users
with completed courses and modules. YOu can also check this via pgadmin if you prefer.

