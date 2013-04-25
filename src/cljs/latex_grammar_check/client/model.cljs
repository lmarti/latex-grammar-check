(ns latex-grammar-check.client.model)

;; use validator during development only!
(defn state-validator [{:keys [errors selected]}]
  (and (every? #(:index %) errors)
       (or (nil? selected) (some #(= % selected) errors))))

(def state (atom {:errors [] :selected nil} 
                 :validator state-validator))

(def action (atom nil))

(defn add-error [error]
  (swap! state update-in [:errors] conj error)
  (reset! action {:type :add :data error}))

(defn clear-selected-error []
  (swap! state assoc :selected nil)
  ;(reset! action {:type :clear-selection :data nil})
  )

(defn remove-error [error]
  (when (= (:selected @state) error) 
    (clear-selected-error))
  (swap! state update-in [:errors] (comp vec (partial remove #(= % error))))
  (reset! action {:type :remove :data error}))

(defn remove-all-errors []
  (doseq [error (:errors @state)]
    (remove-error error)))

(defn select-error [error]
  (swap! state assoc :selected error)
  (reset! action {:type :select :data error}))

;; not needed here ... but this is what I need to make the dom stable in other scenarios
;;  the watcher just needs a way to know if the updated error is the one he is specifically watching
(defn update-error [index error]
  )
