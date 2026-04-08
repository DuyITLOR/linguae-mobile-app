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
  getClozeOptionsWithIds(@Body() body: { ids: number[] }) {
    return this.clozeService.getClozeOptionsWithIds(body.ids);
  }

  @Get("options-with-id")
  getClozeOptionsWithId(@Query('id') id: number){
    return this.clozeService.getClozeOptionsWithId(id);
  }
}