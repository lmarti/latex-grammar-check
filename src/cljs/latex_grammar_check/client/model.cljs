(ns latex-grammar-check.client.model)

(def errors (atom []))

(def action (atom nil))

(defn add-error [error]
  (swap! errors conj error)
  (reset! action {:type :add :data error}))

(defn remove-error [error]
  (swap! errors #(remove (partial = error) %))
  (reset! action {:type :remove :data error}))

(defn clear-errors []
  (reset! errors [])
  (reset! action {:type :clear}))

;; not needed here ... but this is what I need to make the dom stable in other scenarios
;;  the watcher just needs a way to know if the updated error is the one he is specifically watching
(defn update-error [index error]
  )

#_(add-watch action :action-watcher 
  (fn [_ _ _ new-action]
    (println new-action)))