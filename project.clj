(defproject release-4digit-version "0.1.0-SNAPSHOT"
  :description "Leiningen plugin that adds support for 4-digit versions in release/change tasks"
  :url "https://github.com/puppetlabs/lein-release-4digit-version"
  :eval-in-leiningen true

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.7.0"]
                                  [org.clojure/tools.nrepl "0.2.11"]]}})
