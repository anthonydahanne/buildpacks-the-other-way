# buildpacks-the-other-way

## pack demo

```shell
pack buildpack new confoo/java \
    --api 0.8 \
    --path java-buildpack \
    --version 0.0.1 \
    --stacks "*"
```

```shell
tree java-buildpack
java-buildpack
├── bin
│   ├── build
│   └── detect
└── buildpack.toml

```

Add to `detect`:

```shell
if ls *.jar &>/dev/null
then
  echo "Found jar file"
else
  exit 100
fi
```

Add to `build`:
```shell
jar_file=$(ls *.jar)

wget --no-verbose https://download.bell-sw.com/java/17.0.6+10/bellsoft-jre17.0.6+10-linux-amd64.tar.gz
tar xzf bellsoft-jre17.0.6+10-linux-amd64.tar.gz
cat >> "${layers_dir}/launch.toml" <<EOL
[[processes]]
type = "web"
command = './jre-17.0.6/bin/java -jar $jar_file'
default = true
EOL
```

Then, from a folder with a jar file:

```shell
pack build an-image --buildpack=../java-buildpack
```


## spring-boot-maven-plugin demo

## kpack demo

