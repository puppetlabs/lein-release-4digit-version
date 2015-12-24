(ns leiningen.release-4digit-version
  (:require [leiningen.release :as release]))

(defn- version-map
  [major minor patch build snapshot]
  (->> [major minor patch build]
       (map #(Integer/parseInt %))
       (zipmap [:major :minor :patch :build])
       (merge {:snapshot snapshot})))

(defn- version-map->str
  [{:keys [major minor patch build snapshot]}]
  (cond-> (str major "." minor "." patch "." build)
          snapshot (str "-SNAPSHOT")))

(defn- bump-4d-version-map
  [{:keys [major minor patch build]} level]
  (case (keyword (name level))
    :major {:major (inc major) :minor 0 :patch 0 :build 0 :snapshot "SNAPSHOT"}
    :minor {:major major :minor (inc minor) :patch 0 :build 0 :snapshot "SNAPSHOT"}
    :patch {:major major :minor minor :patch (inc patch) :build 0 :snapshot "SNAPSHOT"}
    :build {:major major :minor minor :patch patch :build (inc build) :snapshot "SNAPSHOT"}
    :release {:major major :minor minor :patch patch :build build :snapshot nil}))

(defn wrap-with-4d-bump-version
  "A function intended to be used as a lein hook around `leiningen.release/bump-version`.
  Adds support for bumping versions that contain four components (:major, :minor,
  :patch, :build).  Falls back to the upstream function if the version string does
  not contain 4 components."
  [f version-str & [level]]
  (if-let [[_ major minor patch build snapshot]
           (re-matches #"(\d+)\.(\d+)\.(\d+)\.(\d+)(?:-(SNAPSHOT))?"
                       version-str)]
    (-> (version-map major minor patch build snapshot)
        (bump-4d-version-map (or level release/*level* :build))
        version-map->str)
    (f version-str (or level release/*level* :patch))))

