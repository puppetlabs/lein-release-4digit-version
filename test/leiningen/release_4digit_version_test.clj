(ns leiningen.release-4digit-version-test
  (:require [clojure.test :refer :all]
            [leiningen.release-4digit-version :as release-4d]
            [leiningen.release :as release]))

(deftest test-bump-version
  ;; For compatibility with older version of lein, the plugin overrides the
  ;; default release level with *nil*.  For more details see the comments in the
  ;; `release-4digit-version.plugin` namespace.
  (binding [release/*level* nil]

  ;; Because the 4d bump-version will be used via a
  ;; hook added to the upstream bump-version, we use
  ;; a partial here to test it.
  (let [bump-version (partial release-4d/wrap-with-4d-bump-version
                              release/bump-version)]
    (is (= "1.1.0" (bump-version "1.1.0-SNAPSHOT" :release)))
    (is (= "1.1.0" (bump-version "1.1.0" :release)))
    (is (= "1.1.1-SNAPSHOT" (bump-version "1.1.0-SNAPSHOT")))
    (is (= "1.1.1-SNAPSHOT" (bump-version "1.1.0")))
    (is (= "1.1.1-SNAPSHOT" (bump-version "1.1.0-SNAPSHOT" :patch)))
    (is (= "1.1.1-SNAPSHOT" (bump-version "1.1.0" :patch)))
    (is (= "1.3.0-SNAPSHOT" (bump-version "1.2.0-SNAPSHOT" :minor)))
    (is (= "1.3.0-SNAPSHOT" (bump-version "1.2.0" :minor)))
    (is (= "2.0.0-SNAPSHOT" (bump-version "1.2.0-SNAPSHOT" :major)))
    (is (= "2.0.0-SNAPSHOT" (bump-version "1.2.0" :major)))

    (is (= "1.1.0.0" (bump-version "1.1.0.0-SNAPSHOT" :release)))
    (is (= "1.1.0.0" (bump-version "1.1.0.0" :release)))
    (is (= "1.1.0.1-SNAPSHOT" (bump-version "1.1.0.0-SNAPSHOT")))
    (is (= "1.1.0.1-SNAPSHOT" (bump-version "1.1.0.0")))
    (is (= "1.1.0.1-SNAPSHOT" (bump-version "1.1.0.0-SNAPSHOT" :build)))
    (is (= "1.1.0.1-SNAPSHOT" (bump-version "1.1.0.0" :build)))
    (is (= "1.1.2.0-SNAPSHOT" (bump-version "1.1.1.1-SNAPSHOT" :patch)))
    (is (= "1.1.2.0-SNAPSHOT" (bump-version "1.1.1.1" :patch)))
    (is (= "1.3.0.0-SNAPSHOT" (bump-version "1.2.3.4-SNAPSHOT" :minor)))
    (is (= "1.3.0.0-SNAPSHOT" (bump-version "1.2.3.4" :minor)))
    (is (= "2.0.0.0-SNAPSHOT" (bump-version "1.6.3.4-SNAPSHOT" :major)))
    (is (= "2.0.0.0-SNAPSHOT" (bump-version "1.6.3.4" :major))))))
