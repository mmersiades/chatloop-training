export interface Program {
  name: string; // from Contentful
  sys: { id: string }; // from Contentful
  coursesCollection: { items: Array<Course> }; // from Contentful
  prerequisitePrograms: Array<string> | null; // an array of program ids // from Contentful
  description?: string; // from Contentful
}
export interface Course {
  name: string; // from Contentful
  sys: { id: string }; // from Contentful
  getId() {
    return sys.id
  }
  modulesCollection: { items: Array<Module> }; // from Contentful
  prerequisiteCourses: Array<string> | null; // an array of course ids // from Contentful
  description?: string; // from Contentful
  completion: number; // added by microservice
  incorrectAttempts: number; // added by microservice
}
export interface Module {
  name: string; // from Contentful
  sys: { id: string }; // from Contentful
  moduleMedia: ModuleMedia; // from Contentful
  moduleTask: McqTask; // from Contentful
  isComplete: boolean; // added by microservice
  incorrectAttempts: number; // added by microservice
}
export interface ModuleMedia {
  name: string; // from Contentful
  sys: { id: string }; // from Contentful
  moduleVideo540: { url: string }; // from Contentful
  moduleVideo720: { url: string }; // from Contentful
  moduleVideo1080: { url: string }; // from Contentful
}
export interface McqTask {
  name: string; // from Contentful
  sys: { id: string }; // from Contentful
  instructions: string; // from Contentful
  prompt: string; // from Contentful
  responseAlternatives: Array<string>; // from Contentful
  answer: string; // Actually, this will need to be converted to a number
}