(ns release-4digit-version.plugin
  (:require [robert.hooke :as hooke]
            [leiningen.release :as release]
            [leiningen.release-4digit-version :as release-4d]))

;; NOTE: explanation of gross hacks below:
;;
;; The upstream lein release/change tasks rely on a dynamic var
;; `leiningen.release/*level*` in order for the release task to pass its
;; (optional) command-line argument through to the `change` task.  As of
;; version 2.5.3, the default value of this dynamic var is set to `:patch`,
;; which makes it impossible to determine whether the user actually passed in
;; `:patch` or whether we're just falling back to the default.  For this plugin,
;; ideally the default would be `:build`, and not `:patch`. In upstream leiningen
;; PR #2050 we change the default value of `*level*` to `nil`, and modify
;; the rest of the code to tolerate the `nil` and apply the default value
;; later at runtime.
;;
;; This plugin attempts to be compatible with the current lein and newer versions
;; (assuming PR #2050 is merged).  For future versions, we shouldn't need to do
;; anything except for add a hook that wraps `bump-version`.  However, to be
;; compatible with existing versions, we need to override the default value of
;; `release/*level*` to be `nil`, and, we also need to add a hook to wrap the
;; `release/release` task to tolerate this `nil` value.
;;
;; The hacks are conditional so hopefully the should not be applied when
;; this plugin is used with newer versions of `lein`, after #2050 is merged.

(defn hack-default-dynamic-level-value!
  "Hack that modifies the default value of `leiningen.release/*level*` to be `nil`,
  so that it is possible to determine whether the user passed in a value or whether
  we are falling back to the default.  This hack should only be applied/necessary
  on older versions of leiningen (up to 2.5.3), before PR #2050 is merged."
  []
  (alter-var-root #'release/*level* (constantly nil)))

(defn wrap-release-with-readable-nil
  "Hack that wraps the implementation of the `leiningen.release/release` task to
  support a `nil` value for `leiningen.release/*level*`.  The dirty trick here is
  the implicit knowledge that older versions of leiningen (up to 2.5.3, prior to
  the merge of PR #2050), the `release` task calls `(read-string)` on the `level`
  argument that is passed in to it.  Since we want the default value to be `nil`,
  we sneakily pass in the string `\"nil\"`.  Thus, `read-string` will convert that
  string into an actual `nil` and pass that through.  This hack should only be
  applied/necessary\n  on older versions of leiningen (up to 2.5.3), before
  PR #2050 is merged."
  ([f project]
   (f project "nil"))
  ([f project level]
   (f project level)))

(defn hooks
  "This is the function that is implicitly called by leiningen to register hooks
  for this plugin.  Registers a hook for `leiningen.release/bump-version`, and,
  for older versions of leiningen (up to 2.5.3, prior to the merge of PR #2050),
  applies backward-compatibility hacks to support a default value of `nil` for
  `leiningen.release/*level*`."
  [& args]
  (let [orig-*level* release/*level*]
    ;; this is how we check to see if we are on an older version of leiningen;
    ;; `*level*` defaulted to `:patch`, which made it impossible to determine
    ;; whether or not the user had explicitly passed in a level.
    (when (= :patch orig-*level*)
      ;; if we're on an old version where the default is `:patch`, we hack
      ;; the default to be nil...
      (hack-default-dynamic-level-value!)
      ;; and add the hacky hook around the `release` task.
      (hooke/add-hook #'release/release
                      #'wrap-release-with-readable-nil))
    ;; this is the only "real" feature of the plugin, and the only thing that
    ;; should be required for future versions of lein (after PR #2050 is merged.)
    (hooke/add-hook #'release/bump-version
                    #'release-4d/wrap-with-4d-bump-version)))
