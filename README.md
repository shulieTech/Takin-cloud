# 项目初始化
gradle wrapper

# 项目打包
./gradlew :app:clean :app:build

# 打包到本地仓库
./gradlew :app:clean :app:build :app:publishToMavenLocal

# 发布到远程仓库
./gradlew :app:clean :app:build :app:publish

# 发布到远程仓库，并添加版本号
./gradlew :app:clean :app:build :app:publish -Pversion=1.0.0

# 发布到远程仓库，并添加版本号，并上传到中央仓库
./gradlew :app:clean :app:build :app:publish -Pversion=1.0.0 -PmavenCentralUsername=xxx -PmavenCentralPassword=xxx

# 发布到远程仓库，并添加版本号，并上传到中央仓库，并添加标签
./gradlew :app:clean :app:build :app:publish -Pversion=1.0.0 -PmavenCentralUsername=xxx -PmavenCentralPassword=xxx -Ptag=v1.0.0

# 发布到远程仓库，并添加版本号，并上传到中央仓库，并添加标签，并上传到JCenter
./gradlew :app:clean :app:build :app:publish -Pversion=1.0.0 -P