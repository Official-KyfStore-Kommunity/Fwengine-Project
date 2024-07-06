# How to code a Fwengine Plugin

## Intro

Creating a plugin isn't really hard to do. All you need is to know a little about writing JSON! To begin let's create a plugin that can do these few things:

1. Make the theme "dark-red"
2. Change scripting language to "python"

The full capabilities of plugins can do these four things:

1. Change Theme
2. Change Scripting Language
3. Change Logo Image
4. Change Sprite Image

## Creating The Plugin

Start off by creating a folder. Let's create a plugin called "MyFirstPlugin." Name the folder the name of your plugin and confirm. Next we will have to correctly format the plugin. Begin this process by creating a "plugin.json" in that new folder. The plugin json file should look something similar to:
```json
{
    "name": "your_plugin_name",
    "author": "your_plugin_author",
    "description": "optional_description"
}
```

In this case this is what your plugin should look like:

```json
{
    "name": "MyFirstPlugin",
    "author": "author_goes_here",
    "description": "option_description_here"
}
```

The name and author are required but the description is optional. This json file is used to verify the code and the info about it. Next we will have to create the attributes folder. In the folder directory where you have your "plugin.json" file, create a folder called, "attributes." Also remember to make sure all of the spelling/punctation/capitalization is correct as well. After doing these steps this is what the plugin should look like for now.
```folder-tree
.
├── attributes
|   (Nothing should be in this folder)
└── plugin.json
```

Next create two JSON files in this attributes folder, called, "project.json" and "properties.json." Inside of the properties is where the theme/background_image is placed. You can code something like:

```json
{
    "force-theme": "dark",
    "background-img": "path/to/image.png"
}
```

In this case we will being making the theme "dark-red" with the default background image. So in the properties.json file write something similar to:

```json
{
    "force-theme": "dark-red",
    "background-img": "assets/images/fwengineLogo.png"
}
```

After creating the "properties.json" file, we will now have to create the "project.json" which will contain all of the useful info and environment variables we will need for whenever we create/open a project. The contents of the json file can be of either two ways (only two ways in version 1.3.2):

```json
{
    "isBeingUsed": "false"
}
```
Or: 
```json
{
    "isBeingUsed": "true",
    "scripting-language": "csharp" (this field can also be 'python' as well)
}
```
In this plugin, we will need to use speical properties, so we will enable "isBeingUsed" and use the scripting language python in this case. Write something similar to:

```json
{
    "isBeingUsed": "true",
    "scripting-language": "python"
}
```

Finally we have a plugin. The plugin format should look like:

```folder-tree
.
├── attributes
|   ├── project.json
|   └── properties.json
└── plugin.json
```

After verifing everything is correct, horray! You have created your first Fwengine Plugin. Now place this folder in the plugins folder in Fwengine. When you first start it the plugins folder should be there. Now after doing this you will see all of the changes we did appear on Fwengine!

## Conclusion

Making Fwengine Plugins are very easy and simple. Anyone can make one of these! I hope you enjoyed and learned something about making Fwengine Plugins!