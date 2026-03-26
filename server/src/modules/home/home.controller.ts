import { Controller, Get } from "@nestjs/common";
import { Public } from "../../common";

@Controller('home')
export class HomeController {
  @Public()
  @Get('user')
  async getUser() {

  }