# Mobs Banner

This is a mod add various mobs banners for decoration purpose.

<div align=center><img src="https://github.com/CodeOfArdonia/MobsBanner/img/1.webp" style="width:400px;text-align:center;" alt=""></img></div>

## How to use

### Obtain

- **Mob Drop**: There is a chance to drop banner when a mob is killed(Include players). The chance can be set with
  `/gamerule mobs_banner:drop_chance`. Also, this kind of banner will keep the mob's NBT.
- **Creative Tab**: There is a tab contains all available banners.
- **Command**: `/mob_banner <players> <entity type>` for mob banners.
  `/mob_banner <players> player <base item> <name> <uuid>` for player banners.

### Banner Configure

Sometimes the mobs may have a wrong rotation, unfit scale or just always changing variants. You need to make some
configurations. These configurations are powered by client resource pack system.

Firstly, create file `assets/<entity mod id>/default_banner/<entity id>.json`. Then write the contents below:

```json5
{
  //All keys are optional
  "data": [
    //IMPORTANT: These are only for creative tab! If you change things here they will only refresh after relaunch game!
    //Leave empty for default one.
    {},
    {
      //Leave blank for default spawn egg color
      "primary": -1,
      //Leave blank for default spawn egg color
      "secondary": -1,
      "nbt": {
        //Default NBT, will directly send to entity
      },
      "transform": {
        "scale": 1,
        //Y rotation
        "yaw": 0
      }
    }
    //You can add more for variants
  ],
  //Global transform
  "transform": {
    "scale": 1,
    "yaw": 0
  }
}
```

## FAQ

### I cannot find the banner I want?

Only mobs with a spawn egg (or player) have their banner.

### The player banner don't render skin?

Fill both `name` and `uuid` field.

## Any Questions?

Join our Discord: https://discord.gg/NDzz2upqAk