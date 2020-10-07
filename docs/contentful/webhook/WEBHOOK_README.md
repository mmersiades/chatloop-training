### Introduction
This microservice uses a webhook from Contentful to update the microservice whenever
the training content on Contentful changes.

### Setting up webhook
#### Prerequisites

* A Contentful account and access to the Neon "organization" on Contentful.
* For pointing the webhook at the microservice running locally, you'll need to install
ngrok. See `ngrok.txt` for info about how to do this.
* The url for the microservice you want to point the webhook at. This will probably be:
  * https://user-training.chatloop.org
  * https://staging-user-training.chatloop.org
  * the url provided by ngrok

#### Procedure

* Log in to the Contentful browser UI.
* Go to the `master` **environment** on the `training-3-0` **space**
* Under "Settings", select "Webhooks" (not visible if not on `master`)
* Click "Add Webhook"
* Give the webhook a name. It can be anything, but I recommend keeping it the same as the
microservice's Elastic Beanstalk environment name. Ie:
  * `user-training`
  * `staging-user-training`
* Enter the microservice's url. It should be a `POST` request.
* Select 'Trigger for all events' (default)
* Specify the filter. Keep "Environment ID)" and "equals". The only decision you need
to make is which environment's changes to send to the microservice. The choices are:
  * `master` (the real deal training content)
  * `mock-and-test` (mock programs, courses and modules)
* Click save
* Check it's working by making a change to the content, or adding new content. In the
Contentful UI, the webhook's activity log should be showing `200` http responses. You
can also check by running a graphql query to the microservice from Postman, to see if
it returns the updated data.