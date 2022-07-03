//! Read an mcaselector csv file and convert it into yaml compatible with the ChunkMigrate bukkit
//! plugin. Group chunks by their region.

use std::collections::BTreeMap;
use std::path::Path;
use std::io::BufRead;

use rusqlite::{Connection, Transaction};

struct Chunk {
    x: i64,
    z: i64,
}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let stdin = std::io::stdin();
    let stdin = stdin.lock();

    let mut regions: BTreeMap<(i64, i64), Vec<Chunk>> = BTreeMap::new();

    for line in stdin.lines() {
        let line = line?;

        let mut line = line.split(';');

        let region_x: i64 = line.next().unwrap().parse()?;
        let region_z: i64 = line.next().unwrap().parse()?;
        let chunk_x: i64 = match line.next() {
            Some(x) => x.parse()?,
            None => {
                // When a full region is selected, it is represented by a single line only
                // containing the region coordinate. E.g. if selecting the region 5 | -3 the line
                // would be 5;-3 only.

                let start_x = region_x << 5;
                let start_z = region_z << 5;

                let mut region = Vec::with_capacity(1024);

                for x in start_x..(start_x+32) {
                    for z in start_z..(start_z+32) {
                        region.push(Chunk {
                            x,
                            z,
                        });
                    }
                }

                regions.insert((region_x, region_z), region);

                continue;
            },
        };
        let chunk_z: i64 = line.next().unwrap().parse()?;

        let chunk = Chunk {
            x: chunk_x,
            z: chunk_z,
        };

        let region = regions.entry((region_x, region_z)).or_default();
        region.push(chunk);

        assert!(line.next().is_none());
    }

    eprintln!("Found {} chunks", regions.values().map(Vec::len).sum::<usize>());

    // Write to sqlite
    let db = Path::new("./chunkmigrate118.sqlite");

    if db.exists() {
        panic!("Database file {} already exists", db.display());
    }

    let mut conn = Connection::open(&db)?;

    conn.execute(
        "CREATE TABLE chunks(
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          regionx INTEGER,
          regionz INTEGER,
          x INTEGER,
          z INTEGER
         )
         ", [])?;

    conn.execute(
        "CREATE INDEX index_region ON chunks (regionx, regionz)", [])?;

    let tx = Transaction::new(&mut conn, rusqlite::TransactionBehavior::Exclusive)?;

    {
        let mut st = tx.prepare("INSERT INTO chunks (regionx, regionz, x, z) VALUES (?, ?, ?, ?)")?;

        for ((regionx, regionz), chunks) in regions.iter() {
            for chunk in chunks {
                st.execute([*regionx, *regionz, chunk.x, chunk.z])?;
            }
        }
    }

    tx.commit()?;

    eprintln!("Done");
    Ok(())
}
