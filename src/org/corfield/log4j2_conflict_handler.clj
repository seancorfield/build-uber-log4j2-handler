;; copyright (c) 2021-2023 sean corfield, all rights reserved

(ns org.corfield.log4j2-conflict-handler
  "Provides a conflict handler for use with tools.build.

  (ns build
    (:require [clojure.tools.build.api :as b]
              [org.corfield.log4j2-conflict-handler
               :refer [log4j2-conflict-handler]]))

  (b/uber {... :conflict-handlers log4j2-conflict-handler ...})"
  (:require [clojure.java.io :as io])
  (:import (java.io File InputStream)
           (java.util Collection Enumeration Vector)
           (org.apache.logging.log4j.core.config.plugins.processor PluginCache)))

(set! *warn-on-reflection* true)

(defn log4j2-plugin-merger
  "Conflict handler for clojure.tools.build.api/uber."
  [{:keys [^InputStream in ^File existing]}]
  (let [->enumeration (fn ^Enumeration [^Collection coll]
                        (.elements (Vector. coll)))
        cache (PluginCache.)
        temp  (File/createTempFile "plugins" ".dat")]
    (io/copy in temp :buffer-size 4096)
    (.loadCacheFiles cache (->enumeration (map io/as-url [existing temp])))
    (with-open [os (io/output-stream existing)]
      (.writeCache cache os))
    ;; nothing to write, no state change either:
    nil))

(def log4j2-conflict-handler
  {"^META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat$"
   log4j2-plugin-merger})
