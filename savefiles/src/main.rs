use std::path::PathBuf;

fn main() {
    let dir = std::env::current_dir().unwrap().canonicalize().unwrap();
    let dir_name = dir.file_name().unwrap().to_string_lossy();

    if dir_name == "region" {
        move_to_save();
    } else if dir_name == "SAVE" {
        move_from_save();
    } else {
        panic!("Unknown dir name {}", dir_name);
    }

    println!("Done");
}

fn move_to_save() {
    std::fs::create_dir("./SAVE").unwrap();

    for f in std::fs::read_dir(".").unwrap() {
        let f = f.unwrap().path();

        if !f.is_file() {
            continue;
        }

        let name = f.file_name().unwrap().to_str().unwrap();
        let mut iter = name.split('.');
        // r.0.1.mca
        iter.next().unwrap();
        let x: i64 = iter.next().unwrap().parse().unwrap();
        let z: i64 = iter.next().unwrap().parse().unwrap();
        iter.next().unwrap();
        assert!(iter.next().is_none());

        if !(x < -586 || z < -586 || x > 585 || z > 585) {
            continue;
        }

        let mut target: PathBuf = "./SAVE/".into();
        target.push(&*name);

        std::fs::rename(f, target).unwrap();
    }
}

fn move_from_save() {
    for f in std::fs::read_dir("..").unwrap() {
        let f = f.unwrap().path();

        if !f.is_file() {
            continue;
        }

        let name = f.file_name().unwrap().to_str().unwrap();
        let mut iter = name.split('.');
        // r.0.1.mca
        iter.next().unwrap();
        let x: i64 = iter.next().unwrap().parse().unwrap();
        let z: i64 = iter.next().unwrap().parse().unwrap();
        iter.next().unwrap();
        assert!(iter.next().is_none());

        if !(x < -586 || z < -586 || x > 585 || z > 585) {
            continue;
        }

        println!("Deleting {}", f.display());

        std::fs::remove_file(f).unwrap();
    }

    for f in std::fs::read_dir(".").unwrap() {
        let f = f.unwrap().path();

        let name = f.file_name().unwrap().to_str().unwrap();
        let mut target: PathBuf = "../".into();
        target.push(name);

        std::fs::rename(f, target).unwrap();
    }
}
