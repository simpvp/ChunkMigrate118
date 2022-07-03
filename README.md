Scripts used for migrating simpvp.net to 1.18.

## 1

Shut down server in 1.17.

## 2

Set
```
    below-zero-generation-in-existing-chunks: false
```
in `world-settings.default` in `spigot.yml`.

## 3

Run `savefiles` from `world/region`.

## 4

Run
```
java -jar ~/mcaselector-1.17.3.jar --mode select --region world/region/ --output output.csv --query 'xPos < 18752 AND xPos > -18753 AND zPos < 18752 AND zPos > -18753 AND Status = "full"'
```
from server root.

## 5

Run
```
~/csvtosqlite < ./output.csv
```

## 5

Run
```
mkdir -p plugins/ChunkMigrate118
mv chunkmigrate118.sqlite plugins/ChunkMigrate118/
```

## 6

Ensure ChunkMigrate118.jar is loaded on server.

## 7

Start server on 1.18. Wait until ChunkMigrate118 says it's finished. Do not log in any accounts until this is complete.

## 8

Shut down server when finished and remove ChunkMigrate plugin.

## 9

Set
```
    below-zero-generation-in-existing-chunks: true
```
in `world-settings.default` in `spigot.yml`.

## 10

Run `savefiles` from `world/region/SAVE`.
