# buildpacks-the-other-way

## pack demo


### Author a new buildpack
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

### Use an existing builder

Suggest
```shell
pack builders suggest
```

Build with minimum options
```shell
pack build better-image
```

Build with bindings, additional options
```shell
pack build buildpacks-the-other-way:0.0.1-SNAPSHOT \
  --env BPE_SPRING_PROFILES_ACTIVE=h2 \
  --env BP_JVM_VERSION=17 \
  --env BP_MAVEN_BUILD_ARGUMENTS="-B package -DskipTests"\
  --env SERVICE_BINDING_ROOT=/platform/bindings \
  --volume $PWD/bindings/ca-certificates/:/platform/bindings/my-certificates \
  --buildpack=gcr.io/paketo-buildpacks/azul-zulu \
  --buildpack paketo-buildpacks/java
```

### Get Sbom

```shell
pack sbom download buildpacks-the-other-way:0.0.1-SNAPSHOT --output-dir ./app-sbom
cat app-sbom/layers/sbom/launch/sbom.legacy.json | jq -r .[].metadata.dependencies
```

## Integrations

### GitHub actions demo

https://github.com/anthonydahanne/newsy-mastodon/actions/runs/4154365331/jobs/7186707526


### spring-boot-maven-plugin demo

```shell
./mvnw clean -Pbuildpack spring-boot:build-image -DskipTests
```

### kpack demo

* Read https://github.com/pivotal/kpack/blob/main/docs/tutorial.md
* install the CLI: https://github.com/vmware-tanzu/kpack-cli
* start installing kpack in your cluster: `kubectl apply -f release-0.9.2.yaml`
* install all other yaml files

Then you can submit a new image to kpack:

```yaml
apiVersion: kpack.io/v1alpha2
kind: Image
metadata:
  name: buildpacks-the-other-way
  namespace: default
spec:
  build:
    env:
      - name: "BPE_SPRING_PROFILES_ACTIVE"
        value: "h2"
  tag: anthonydahanne/buildpacks-the-other-way
  serviceAccountName: tutorial-service-account
  builder:
    name: my-builder
    kind: Builder
  source:
    git:
      url: https://github.com/anthonydahanne/buildpacks-the-other-way
      revision: 3330be0e88baa4b3be026765f751ae072070f1b6

```

