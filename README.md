# Bloodshed

A Minecraft mod for adding blood to the game.

**Written by Deepseek AI for Minecraft v1.18.2 Fabric.**

## History

I wanted a gore mod and couldn't find one for Fabric on v1.18.2, but I did find a mod by [Rasus](https://github.com/TheTarasus/RasusBloodshed).  His mod only had bleeding for players.  I took that, modified it to apply to all mobs, and added a config to control how it worked.  I also added a fadeout on the blood splatters.

## Configuration

```json
{
  "enabled": true,
  "bloodFadeTime": 30,
  "goreValue": 1.0,
  "noBleedEntities": [
    "minecraft:skeleton",
    "minecraft:stray",
    "minecraft:wither_skeleton"
  ]
}
```

## Help

If you want to force remove blood you can use this:

```
/fill ~-24 ~-5 ~-24 ~24 ~5 ~24 air replace bloodshed:blood
```

## Credits

- [RasusBloodshed](https://github.com/TheTarasus/RasusBloodshed) - original mod