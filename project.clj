(defproject lein-release-4digit-version "0.1.0"
  :description "Leiningen plugin that adds support for 4-digit versions in release/change tasks"
  :url "https://github.com/puppetlabs/lein-release-4digit-version"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :eval-in-leiningen true

  :lein-release {:scm         :git
                 :deploy-via  :lein-deploy}
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/clojars_jenkins_username
                                     :password :env/clojars_jenkins_password
                                     :sign-releases false}]]

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.7.0"]
                                  [org.clojure/tools.nrepl "0.2.11"]]}})
