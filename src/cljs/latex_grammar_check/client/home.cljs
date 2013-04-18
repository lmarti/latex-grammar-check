(ns latex-grammar-check.client.home
  (:require [dommy.core :refer [listen! replace-contents! append!]]
            [dommy.attrs :refer [toggle-class! hidden? toggle!]]
            [dommy.template :as template]
            [domina :as dom]
            [ajax.core :as ajax]
            [latex-grammar-check.client.codemirror :as cm])
  (:require-macros [dommy.macros :refer [sel1 sel]]))

(def editor (atom nil))

(def grammar-check-marks (atom []))

(def container (sel1 :#container))

(defn grammar-error-tooltip [message replacements]
  [:span {:style {:display "none"}}
   [:lu 
    (for [r replacements]
     [:li r])]])

(defn grammar-error [text tooltip]
  [:span {:classes ["grammar-checker-problem"]} 
   tooltip
   text])

(defn handle-grammar-check-result [coll]
  (replace-contents! (sel1 :#check-grammar-result)
                     (map #(template/node [:li (:message %)]) coll))
  
  (doseq [mark @grammar-check-marks] (cm/clear-mark @editor mark))
  (reset! grammar-check-marks [])
  
  ;(.setGutterMarker @editor 1 "grammar-checker-problem-gutter" (template/node [:span "x"]))
  
  (doseq [r coll]
    (let [{:keys [line column end-line end-column message suggested-replacements]} r
          from {:line line :ch (dec column)}
          to {:line end-line :ch (dec end-column)}
          text (cm/get-range @editor from to)
          tooltip (template/node (grammar-error-tooltip message suggested-replacements))
          element (template/node (grammar-error text tooltip))]
      (->> (cm/mark-text @editor from to
                         {:clearOnEnter true
                          :replacedWith element
                          })
           (swap! grammar-check-marks conj))
      (listen! element :mouseover #(toggle! tooltip true))
      (listen! element :mouseout #(toggle! tooltip false))
      )))

(defn handle-check-grammar [e]
  (cm/focus @editor)
  (ajax/POST "/check-grammar"
             {:handler handle-grammar-check-result
              :params  {:latex-markup (cm/get-value @editor)}}))

(defn handle-dumb-check-grammar [e]
  (cm/focus @editor)
  (ajax/POST "/dumb-check-grammar"
             {:handler handle-grammar-check-result
              :params  {:latex-markup (cm/get-value @editor)}}))

(defn handle-extract-text-result [text]
  (cm/set-value @editor text))

(defn handle-extract-text [e]
  (cm/focus @editor)
  (ajax/POST "/extract-text"
             {:handler handle-extract-text-result
              :params  {:latex-markup (cm/get-value @editor)}}))

(defn ^:export init []
  (-> container
      (append! (template/node [:textarea#latex-markup "A sentence with a error in the Hitchhiker's Guide tot he Galaxy"]))
      (append! (template/node [:button#check-grammar "Check Grammar"]))
      (append! (template/node [:button#dumb-check-grammar "Dumb Check Grammar"]))
      (append! (template/node [:button#extract-text "Extract Text"]))
      (append! (template/node [:lu#check-grammar-result])))
  (reset! editor (cm/create-editor (sel1 :#latex-markup) {:lineNumbers true
                                                          :mode { :name "stex" }
                                                          :tabMode "indent"
                                                          :highlightSelectionMatches true
                                                          ;;:styleActiveLine true
                                                          :gutters ["grammar-checker-problem-gutter"]}))  
  (listen! (sel1 :#check-grammar) :click handle-check-grammar)
  (listen! (sel1 :#dumb-check-grammar) :click handle-dumb-check-grammar)
  (listen! (sel1 :#extract-text) :click handle-extract-text))