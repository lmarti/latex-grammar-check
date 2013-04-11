(ns latex-grammar-check.service
  (:require [io.pedestal.service.http :as bootstrap]
            [io.pedestal.service.http.route :as route]
            [io.pedestal.service.http.body-params :as body-params]
            [io.pedestal.service.http.route.definition :refer [defroutes]]
            [ring.util.response :as ring-resp]
            [io.pedestal.service.log :as log]
            [latex-grammar-check.util :refer [check-grammar]]
            [latex-grammar-check.latex :refer [extract-text tokenize]]))

(defn handle-check-grammar
  [request]
  (let [latex-markup (get-in request [:params "latex-markup"])]
    (log/debug :latex-markup latex-markup)
    (->> (check-grammar latex-markup)
         (bootstrap/edn-response))
    ))

(defn handle-extract-text
  [request]
  (let [latex-markup (get-in request [:params "latex-markup"])]
    (log/debug :latex-markup latex-markup)
    (->> latex-markup
         (tokenize)
         (extract-text)
         (bootstrap/edn-response))
    ))

(defn home-page
  [request]
  (ring-resp/redirect "home.html"))

(defroutes routes
  [[["/" {:get home-page}
     
     ;; Set default interceptors for /about and any other paths under /
     ^:interceptors [(body-params/body-params)]
     ["/check-grammar" {:post handle-check-grammar}]
     ["/extract-text" {:post handle-extract-text}]]]])

;; You can use this fn or a per-request fn via io.pedestal.service.http.route/url-for
(def url-for (route/url-for-routes routes))

;; Consumed by latex-grammar-check.server/create-server
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; :bootstrap/interceptors []
              ::bootstrap/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::boostrap/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::bootstrap/resource-path "/public"

              ;; Either :jetty or :tomcat (see comments in project.clj
              ;; to enable Tomcat)
              ;;::bootstrap/host "localhost"
              ::bootstrap/type :jetty
              ::bootstrap/port (or (Integer. (get (System/getenv) "PORT" "8080")) 
                                   8080)})
