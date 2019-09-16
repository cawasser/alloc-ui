(ns alloc-ui.routes.services
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [alloc-ui.middleware.formats :as formats]
    [alloc-ui.middleware.exception :as exception]
    [ring.util.http-response :refer :all]
    ;[clojure.java.io :as io]
    [alloc-ui.db.core :as db]
    [alloc-ui.routes.grid-support :as gs]
    [trptcolin.versioneer.core :as version]))

(def grid-spec [[#{keyword?}]])

(defn get-version []
  (version/get-version "alloc-ui" "alloc-ui" "Not Found"))


(defn get-sha []
    (subs (version/get-revision "alloc-ui" "alloc-ui" "Not Found") 0 5))


(defn service-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodies
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]

   ["/grid"
    {:get {:summary "get current grid"
           :responses {200 {:body {:service-version string?
                                   :result string?}}}
           :handler (fn [_]
                      (prn "GET /grid")
                      (prn "server version: " (get-version) ":" (get-sha))
                      {:status 200
                       :body {:service-version (get-version)
                              :service-sha (get-sha)
                              :result (pr-str (gs/to-grid (db/get-current-grid)))}})}}]


   ["/request"
     {:post {:summary "try to put requests into the current grid"
             :parameters {:body {:requests string?}}
             :responses {200 {:body {:service-version string?
                                     :result string?}}}
             :handler (fn [{{{:keys [requests]} :body} :parameters}]
                        (prn "POST /api/request" requests)
                        {:status 200
                         :body {:service-version (get-version)
                                :service-sha (get-sha)
                                :result
                                (pr-str
                                  (gs/apply-requests-to-grid
                                    (clojure.edn/read-string
                                      requests)))}})}}]])



(comment
  (System/setProperty "bar.version" "1.2.3-SNAPSHOT")
  (version/get-version "alloc-ui" "alloc-ui")
  (subs (version/get-revision "alloc-ui" "alloc-ui") 0 5)
  (System/getProperty "alloc-ui.version"))
