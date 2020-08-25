### FetchUserProgram

##### Query
```
query {
  userProgram(id: "programid") {
    id
    userCourses {
      id
      completion
    }
  } 
}
```

##### Example response
```
{ userPrograms: [
  {
    id: "programId1"
    userCourses: [
      { id: "courseId1", completion: 1 },
      { id: "courseId2", completion: 1 },
      { id: "courseId3", completion: 0.5 },
    ]
  },
  {
    id: "programId2"
    description: "A mock userProgram with two courses"
    userCourses: [
      { id: "courseId4", completion: 0 },
      { id: "courseId5", completion: 0 },
    ]
  }
]
  
}
```

### FetchUserCourse

##### Query
```
query {
  userCourse(id: "courseid") {
    id
    completion
    incorrectAttempts
    courseResult
    userModules {
      id
      isComplete
      incorrectAttempts
    }
  } 
}
```

##### Example response
```
{ userCourse: 
    id: "courseId1"
    completion: 1
    incorrectAttempts: 1
    courseResult: "COURSE_PASSED"
    userModules [
      { id: "moduleId1", isComplete: true, incorrectAttempts: 1 },
      { id: "moduleId2", isComplete: true, incorrectAttempts: 0 },
    ]
}
```

### ResetUserCourse

##### Mutation

```
mutation {
  resetUserCourse(id: "courseid") {
    id
    completion
    incorrectAttempts
    courseResult
    modules {
      id
      isComplete
      incorrectAttempts
    }
  }
}
```

##### Example response
```
{ userCourse: 
    id: "courseId1"
    completion: 0
    incorrectAttempts: 0
    courseResult: "NO_RESULT_YET"
    modules [
      { id: "moduleId1", isComplete: false, incorrectAttempts: 0 },
      { id: "moduleId2", isComplete: false, incorrectAttempts: 0 },
    ]
}
```

### Schema

```
enum CourseResult {
    NO_RESULT_YET
    COURSE_FAILED
    COURSE_PASSED
}

type UserProgram {
    id: ID!
    name: String!
    description: String
    userCourses: [UserCourse]
}

type UserCourse {
    id: ID!
    name: String!
    description: String
    completion: Float!
    incorrectAttempts: Int!
    userModules: [UserModule]
}

type UserModule {
    id: ID!
    name: String!
    isComplete: Boolean!
    incorrectAttempts: Int!
    task: Task
}

type Task {
    id: ID!
    name: String!
}

type Mutation {
    resetUserCourse(courseId:String):Course
    taskAttempt(courseId:String!, moduleId:String!, isCorrect:Boolean!):Course
}

type Query {
    fetchUserProgram(id:String!): Program
    fetchUserCourse(id:String!): Course
}
```