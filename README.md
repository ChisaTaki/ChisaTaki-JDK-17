# ChisaTaki Bot

The ChisaTaki bot is a bot written for the ChisaTaki server, and its exclusive use. This bot should never be allowed into another server as it could cause unforeseen consequences. However, you are allowed to fork this repo and use the code in your own bot.

### Maven Dependencies

To avoid formatting conflicts between developer environments, each developer will have their own `pom.xml` by following the steps below:

**Setting up the pom.xml**

1. Create a `pom.xml` file at project root level
2. Copy the content of `pom.sample.xml` into the new file created
3. Save and do a maven update (replit does it automatically)

**Updating the pom.xml**

1. Add the necessary dependencies as usual in your _own_ `pom.xml`
2. Ensure your changes produce the desired output
3. Copy and paste the lines you added in step 1 into the `pom.sample.xml`
4. Resume the usual development worflow until merge (commit changes, create PR, merge branch)
5. Once the branch is merged into the main branch, you may want to notify the other devs so they can update their respective `pom.xml` after pulling your changes

## Deployment

How to build the ChisaTaki Bot

### Building the ChisaTaki.jar

**Local IDE**

1. Open a File Explorer window and navigate to your target classes folder (i.e.: `target/classes`)
2. Perform a maven clean
3. Confirm that no compilation errors appear
4. Ensure the `.class` files were regenerated
5. In your target classes folder, delete any existing files/folders that do not match the repository's package name (i.e.: "META-INF") if any
6. Copy-paste the `Manifest.txt` file found at the root of the project
7. Open a terminal window and type out the following `jar -cvfm ChisaTaki.jar Manifest.txt dev/*`
8. Press Enter and wait for the jar file to build

## Authors

-   [@Hacking Pancakez](https://github.com/Hacking-Pancakez)
-   [@shimdevkun](https://github.com/shimdevkun)
