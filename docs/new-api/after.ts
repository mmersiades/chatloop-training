export interface Program {
  name: string; // from Contentful
  id: string; // from Contentful, but de-nested
  coursesCollection:  Array<Course>  // from Contentful, but de-nested
  prerequisitePrograms: Array<string> | null; // an array of program ids // from Contentful
  description?: string; // from Contentful
}
export interface Course {
  name: string; // from Contentful
  id: string; // from Contentful, but de-nested
  modulesCollection: Array<Module>; // ffrom Contentful, but de-nested
  prerequisiteCourses: Array<string> | null; // an array of course ids // from Contentful
  description?: string; // from Contentful
  completion: number; // added by microservice
  incorrectAttempts: number; // added by microservice
}
export interface Module {
  name: string; // from Contentful
  id: string; // from Contentful, but de-nested
  moduleMedia: ModuleMedia; // from Contentful
  moduleTask: McqTask; // from Contentful
  isComplete: boolean; // added by microservice
  incorrectAttempts: number; // added by microservice
}
export interface ModuleMedia {
  name: string; // from Contentful
  id: string; // from Contentful, but de-nested
  moduleVideo540: string; // from Contentful, but de-nested
  moduleVideo720: string; // from Contentful, but de-nested
  moduleVideo1080: string; // from Contentful, but de-nested
}
export interface McqTask {
  name: string; // from Contentful
  id: string; // from Contentful, but de-nested
  instructions: string; // from Contentful
  prompt: string; // from Contentful
  responseAlternatives: Array<string>; // from Contentful
  answer: string; // Actually, this will need to be converted to a number
}