(ns leiningen.release-4digit-version
  (:require [leiningen.release :as release]))

(defn- version-map
  [major minor patch build qualifier snapshot]
  (->> [major minor patch build]
       (map #(Integer/parseInt %))
       (zipmap [:major :minor :patch :build])
       (merge {:qualifier qualifier
               :snapshot snapshot})))

(defn- version-map->str
  [{:keys [major minor patch build qualifier snapshot]}]
  (cond-> (str major "." minor "." patch "." build)
          qualifier (str "-" qualifier)
          snapshot (str "-" snapshot)))

(defn- bump-4d-version-map
  [{:keys [major minor patch build qualifier snapshot]} level]
  (let [level (or level
                  (if qualifier :qualifier)
                  :build)]
    (case (keyword (name level))
      :major {:major (inc major) :minor 0 :patch 0 :build 0 :qualifier nil :snapshot "SNAPSHOT"}
      :minor {:major major :minor (inc minor) :patch 0 :build 0 :qualifier nil :snapshot "SNAPSHOT"}
      :patch {:major major :minor minor :patch (inc patch) :build 0 :qualifier nil :snapshot "SNAPSHOT"}
      :build {:major major :minor minor :patch patch :build (inc build) :qualifier nil :snapshot "SNAPSHOT"}
      :qualifier {:major major :minor minor :patch patch :build build
                  :qualifier (release/next-qualifier qualifier)
                  :snapshot "SNAPSHOT"}
      :release (merge {:major major :minor minor :patch patch :build build}
                      (if snapshot
                        {:qualifier qualifier :snapshot nil}
                        {:qualifier nil :snapshot nil})))))

(defn wrap-with-4d-bump-version
  "A function intended to be used as a lein hook around `leiningen.release/bump-version`.
  Adds support for bumping versions that contain four components (:major, :minor,
  :patch, :build).  Falls back to the upstream function if the version string does
  not contain 4 components."
  [f version-str & [level]]
  (if-let [[_ major minor patch build qualifier snapshot]
           (re-matches #"(\d+)\.(\d+)\.(\d+)\.(\d+)(?:-(?!SNAPSHOT)([^\-]+))?(?:-(SNAPSHOT))?"
                       version-str)]
    (-> (version-map major minor patch build qualifier snapshot)
        (bump-4d-version-map (or level release/*level*))
        version-map->str)
    (f version-str (or level
                       release/*level*
                       (if (:qualifier (release/string->semantic-version version-str))
                         :qualifier)
                       :patch))))

