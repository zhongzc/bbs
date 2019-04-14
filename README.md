## a note for building

`local_maven_repo` contains two modules compiled from [this project](https://github.com/zhongzc/sst).

To keep these jars in your maven repository updated, please:

- delete `.m2/repository/com/gaufoo` , and then 

- reimport maven project before building.

