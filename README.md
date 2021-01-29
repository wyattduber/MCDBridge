# MCDBridge
This is a plugin that can run commands on a Minecraft server when a specified role is given on a discord server. The Idea is to give rewards of some kind on a Minecraft server when a user gets a donation role on a discord server, such as Discord Nitro or Patreon.

Current Features Include:
• Set up an Arbitrary Amount of Discord Roles!
• Each Role has an Arbitrary Amount of Commands to run when the Role is Added and Removed!

And that's it.... for now.

This plugin is only in the early stages of beta, and I am looking to expand it's features as I progress.
This is my first attempt at a Minecraft plugin, and it seems to be working for what it's currently intended for.

How to set up:
1. Get the bot token for the discord bot you'd like to use.
2. Get the server ID for the discord server you're using.
3. Get the Role ID's for the roles you would like to use.

Configuration:
Simply define how many roles you would like to have in the "roles:" category, then make a seperate category following the example formats with the same title as the name that you gave it in the "roles:" category. Then paste the role ID's in the respective "role-id:" spots and reload the plugin. Then add the commands you would like to run when the role is given, and taken away. You can use "%USER%" as a placeholder for the user's Minecraft username as well!

Here's an example:

Code (YAML):
bot-token: "BOTTOKEN"
server-id: "000000000000000000"

roles:
 - patreon
  - nitro

patreon:
  role-id: "000000000000000000"
  add-commands:
   - "say %USER% was given the Patreon Role!"
    - "pay %USER% 100"
  remove-commands:
   - "say %USER% was removed from the Patreon Role"
    - "smite %USER%"

nitro:
  role-id: "000000000000000000"
  add-commands:
   - "say %USER% was given the Nitro Role!"
    - "pay %USER% 100"
  remove-commands:
   - "say %USER% was removed from the Nitro Role"
    - "smite %USER%"

