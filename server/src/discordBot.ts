import {Client, Events, REST, Routes} from "discord.js";
import {Environment} from "./environment";
import {DeviceManager} from "./deviceManager";
import {ActionManager} from "./actionManager";

export class DiscordBot {
  rest: REST | undefined;
  client: Client | undefined;

  commands: {name: string, description: string}[] = [
    {
      name: 'ping',
      description: 'Replies with Pong!',
    },
    {
      name: 'lights-out',
      description: 'Turns all of the lights (yo) off',
    },
    {
      name: 'lights-on',
      description: "Turns all lights (yo) on",
    },
    {
      name: 'windows-close',
      description: "Closes all of the windows yo"
    },
    {
      name: 'windows-open',
      description: "Opens all of the windows yo"
    },
    {
      name: 'heater-garbus',
      description: "Shut off all heaters into chambers",
    },
    {
      name: 'heater-komora',
      description: "Turn on all heaters into chambers",
    }
  ];

  private TOKEN: string = Environment.DISCORD_TOKEN
  private CLIENT_ID: string = Environment.DISCORD_CLIENT_ID;

  private isGit: boolean = false;

  public isClientGit(): boolean {
    return this.isGit;
  }

  private readonly admin_intents = 8;

  constructor(public actionManger: ActionManager) {
    console.group('Discord Bot');
    try {
      this.rest = new REST({ version: '10' }).setToken(this.TOKEN);
      this.client = new Client({ intents: this.admin_intents });

      this.setupErrorHandling();
      this.setupOnReady();
      this.setupCommands();

      void this.syncCommands();
    } catch (e) {
      console.error('Failed to initialize Discord bot:', e);
      this.isGit = false;
    } finally {
      console.groupEnd();
    }
  }


  private setupErrorHandling(): void {
    this.client?.on(Events.Error, (error) => {
      console.error('Discord client error:', error);
    });
  }

  private async syncCommands(){
    try {
      console.log('Started refreshing application (/) commands.');
      await this.rest?.put(Routes.applicationCommands(this.CLIENT_ID), { body: this.commands })
      console.log('Successfully reloaded application (/) commands.');

    } catch (error) {
      console.error('Failed to sync application commands:', error);
      this.isGit = false;
    }
  }

  private setupOnReady(): void {
    this.client?.on(Events.ClientReady, (readyClient) => {
      console.log(`Logged in as ${readyClient.user.tag}!`);
    });
  }


  private setupCommands(): void {
    this.client?.on(Events.InteractionCreate, async (interaction) => {
      if (!interaction.isChatInputCommand()) return;

      try {
        switch (interaction.commandName) {
          case 'ping':
            await interaction.reply('Pong!');
            break;
          case 'lights-out':
            await this.actionManger.publishAction(ActionManager.everyIDForModule("smart_led"), "off", {})
            await interaction.reply('Kanye West');
            break;
          case 'lights-on':
            await this.actionManger.publishAction(ActionManager.everyIDForModule("smart_led"), "on", {})
            await interaction.reply('Micheal Jordan');
            break;
          case 'windows-close':
            await this.actionManger.publishAction(ActionManager.everyIDForModule("window"), "close", {})
            await interaction.reply('Zamknięto Wszystkie Okna!');
            break;
          case 'windows-open':
            await this.actionManger.publishAction(ActionManager.everyIDForModule("window"), "open", {})
            await interaction.reply('Otwarto Wszystkie Okna!');
            break;
          case 'heater-garbus':
            await this.actionManger.publishAction(ActionManager.everyIDForModule("heater"), "off", {})
            await interaction.reply('Wszystkie Garbusy do Komory!');
            break;
          case 'heater-komora':
            await this.actionManger.publishAction(ActionManager.everyIDForModule("heater"), "on", {})
            await interaction.reply('Wszystkie Komory!');
            break;
        }
      } catch (error) {
        console.error(`Error handling /${interaction.commandName}:`, error);
      }
    });
  }

  public async login(): Promise<void> {
    try {
      await this.client?.login(this.TOKEN);
      this.isGit = true;
    } catch (e) {
      console.error('Discord login failed:', e);
      this.isGit = false;
    }
  }

}