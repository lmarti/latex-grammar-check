(ns latex-grammar-check.client.home
  (:require [dommy.core :refer [listen! replace-contents! append! descendant?]]
            [dommy.attrs :refer [toggle-class! hidden? toggle!]]
            [dommy.template :as template]
            [ajax.core :as ajax]
            [latex-grammar-check.client.codemirror :as cm]
            [latex-grammar-check.client.model :as model]
            [latex-grammar-check.client.text-mark :as tm]
            [latex-grammar-check.client.error-message :as em])
  (:require-macros [dommy.macros :refer [sel1 sel]]))

(def editor (atom nil))

(def container (sel1 :#container))

(defn add-index [errors]
  (->> errors
       (map-indexed (fn [index error] (assoc error :index index)))
       vec))

(defn handle-grammar-check-result [errors]
  (model/remove-all-errors)
  (doseq [error (add-index errors)]
    (model/add-error error)))

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
      (append! (template/node [:textarea#latex-markup "A \\LaTeX sentence with \\emph{a} error in the \\textbf{Hitchhiker's Guide} tot he Galaxy"]))
      (append! (template/node 
       [:div#buttons.text-center
        [:div#buttons.btn-group
          [:a#check-grammar.btn  
            [:i.icon-check]
            [:span  " Check Grammar"]]
          [:a#dumb-check-grammar.btn "Dumb Check Grammar"]
          [:a#extract-text.btn "Extract Text"]]]))
      (append! (template/node [:div#check-grammar-result])))
  (reset! editor (cm/create-editor (sel1 :#latex-markup) {:lineNumbers true
                                                          :mode { :name "stex" }
                                                          :tabMode "indent"
                                                          :highlightSelectionMatches true
                                                          ;;:styleActiveLine true
                                                          :gutters ["grammar-checker-problem-gutter"]}))  
  (listen! (sel1 :#check-grammar) :click handle-check-grammar)
  (listen! (sel1 :#dumb-check-grammar) :click handle-dumb-check-grammar)
  (listen! (sel1 :#extract-text) :click handle-extract-text)
  (em/init)
  (tm/init @editor))