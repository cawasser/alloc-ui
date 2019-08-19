(ns user
  (:require
    [alloc-ui.config :refer [env]]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mount.core :as mount]
    [alloc-ui.figwheel :refer [start-fw stop-fw cljs]]
    [alloc-ui.core :refer [start-app]]
    [alloc-ui.db.core]
    [conman.core :as conman]
    [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'alloc-ui.core/repl-server))

(defn stop []
  (mount/stop-except #'alloc-ui.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn restart-db []
  (mount/stop #'alloc-ui.db.core/*db*)
  (mount/start #'alloc-ui.db.core/*db*)
  (binding [*ns* 'alloc-ui.db.core]
    (conman/bind-connection alloc-ui.db.core/*db* "sql/queries.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))


