import { Controller, Get, Post, Query, Body } from "@nestjs/common";

import { ClozeService } from "./cloze.service";

@Controller('cloze')
export class ClozeController {
  constructor(private readonly clozeService: ClozeService) {}

  @Get('questions')
  getClozeQuestions(){
    return this.clozeService.getClozeQuestions();
  }
  
  @Post("options-with-ids")
  getClozeOptionsWithIds(@Body() ids: number[] ) {
    return this.clozeService.getClozeOptionsWithIds(ids);
  }

  @Get("options-with-id")
  getClozeOptionsWithId(@Query('id') id: number){
    return this.clozeService.getClozeOptionsWithId(id);
  }

 @Get('questions-by-topic-id')
 getClozeQuestionByTopicId(@Query('topicId') topicId: string){
  return this.clozeService.getClozeQuestionByTopicId(topicId);
 }
}