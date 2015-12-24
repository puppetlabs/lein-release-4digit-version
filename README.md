# release-4digit-version

A Leiningen plugin that can be used with the `release` and `change`
tasks, but supports 4-digit version numbers (e.g. 1.2.3.4,
1.2.3.4-SNAPSHOT).

Uses leiningen hooks to wrap the appropriate functions from the
`leiningen.release` namespace, so you don't need to do anything other
than add the plugin to your `:plugins` list in your project.

Backwards compatible, so it does not change the behavior of `release`
or `change` if your project uses 3-digit version numbers.

Note that the upstream `change` task supports levels `:major`,
`:minor`, `:patch`, and `:release`;  This plugin adds a level
`:build`, which maps to the 4th digit in the version string.  If you
do not specify a level when calling `release` or `change`, the default
is `:patch` if your version number has 3 digits, and `:build` if your
version number has 4 digits.

## Usage

Put `[release-4digit-version "0.1.0-SNAPSHOT"]` into the `:plugins` vector of
your project.clj.

After adding the plugin dependency, you may use the `release` and
`change` tasks as usual:

For 4-digit version numbers:
```
$ lein pprint :version
"5.4.0.0-SNAPSHOT"
$ lein release && lein pprint :version
"5.4.0.1-SNAPSHOT"
$ lein release :patch && lein pprint :version
"5.4.1.0-SNAPSHOT"
$ lein change version "leiningen.release/bump-version" && lein pprint :version
"5.4.1.1-SNAPSHOT"
$ lein change version "leiningen.release/bump-version" :build && lein pprint :version
"5.4.1.2-SNAPSHOT"
```

For 3-digit version numbers:
```
$ lein pprint :version
"5.4.0-SNAPSHOT"
$ lein release && lein pprint :version
"5.4.1-SNAPSHOT"
$ lein release :minor && lein pprint :version
"5.5.0-SNAPSHOT"
$ lein change version "leiningen.release/bump-version" && lein pprint :version
"5.5.1-SNAPSHOT"
$ lein change version "leiningen.release/bump-version" :patch && lein pprint :version
"5.5.2-SNAPSHOT"
```

