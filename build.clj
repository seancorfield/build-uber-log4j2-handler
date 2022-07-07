(ns build
  (:require [org.corfield.build :as bb]))

(def lib 'com.github.seancorfield/build-uber-log4j2-handler)
(def version "0.1.5")
(defn get-version [opts]
  (str version (when (:snapshot opts) "-SNAPSHOT")))

(defn jar [opts]
  (-> opts
      (assoc :lib lib :version (get-version opts))
      bb/clean
      (assoc :src-pom "template/pom.xml")
      bb/jar))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (-> opts
      (assoc :lib lib :version (get-version opts))
      bb/deploy))
