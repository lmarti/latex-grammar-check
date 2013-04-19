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

(defn grammar-error-tooltip [message replacements f]
  [:div
   [:lu
    (for [r replacements]
     (let [e (template/node [:li r])]
       (listen! e :click #(f r))
       e))]])

(defn grammar-error [text tooltip]
  [:span {:classes ["grammar-checker-problem"]} text])

(defn apply-replacement-fn [mark-atom]
  (fn [replacement]
    (let [{from "from" to "to"} (js->clj (cm/find-mark @mark-atom))]
      (cm/replace-range @editor replacement from to))))

(defn handle-grammar-check-result [coll]
  
  ; errors are listed as a table with line number and description
  (replace-contents! (sel1 :#check-grammar-result)
                     ;;[(template/node [:tr  [:td "##"] [:td "Description"]])
                     (template/node [:tbody 
                                     (map #(template/node 
                                            [:tr [:td (str (:line %))] [:td (:message %)]]) coll)]))
  
  (doseq [mark @grammar-check-marks] (cm/clear-mark mark))
  (reset! grammar-check-marks [])
  
  ;(.setGutterMarker @editor 1 "grammar-checker-problem-gutter" (template/node [:span "x"]))
  
  (doseq [r coll]
    (let [{:keys [line column end-line end-column message suggested-replacements]} r
          from {:line line :ch (dec column)}
          to {:line end-line :ch (dec end-column)}
          text (cm/get-range @editor from to)
          mark-atom (atom nil)
          tooltip (template/node (grammar-error-tooltip message suggested-replacements (apply-replacement-fn mark-atom)))
          element (template/node (grammar-error text tooltip))]
      
      (.popover (js/jQuery element) (clj->js {:title message 
                                              :content (.-outerHTML tooltip) ;;"<a href='http://www.google.com'>Google</a>"
                                              :trigger "manual"
                                              :html true
                                              :placement "bottom"}))
      
      (->> (cm/mark-text @editor from to
                         {:clearOnEnter true
                          :replacedWith element
                          })
           (reset! mark-atom)
           (swap! grammar-check-marks conj))
      (listen! element :mouseover #(.popover (js/jQuery element) "show"))
      ;; add event handler after show :)
      ;;(listen! element :mouseout #(.popover (js/jQuery element) "hide"))
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
      (append! 
        (template/node [:div#buttons.text-center
          (template/node [:div#buttons.btn-group
            (template/node [:a#check-grammar.btn  
                           (template/node [:i.icon-check ]) 
                           (template/node [:span  " Check Grammar"])])
            (template/node [:a#dumb-check-grammar.btn "Dumb Check Grammar"])
            (template/node [:a#extract-text.btn "Extract Text"])])]))
      (append! 
       (template/node [:table#check-grammar-result {:class "table table-condensed table-striped table-hover"}])))
  (reset! editor (cm/create-editor (sel1 :#latex-markup) {:lineNumbers true
                                                          :mode { :name "stex" }
                                                          :tabMode "indent"
                                                          :highlightSelectionMatches true
                                                          ;;:styleActiveLine true
                                                          :gutters ["grammar-checker-problem-gutter"]}))  
  (listen! (sel1 :#check-grammar) :click handle-check-grammar)
  (listen! (sel1 :#dumb-check-grammar) :click handle-dumb-check-grammar)
  (listen! (sel1 :#extract-text) :click handle-extract-text))