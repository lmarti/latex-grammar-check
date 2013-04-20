(ns latex-grammar-check.client.home
  (:require [dommy.core :refer [listen! replace-contents! append! descendant?]]
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
  [:div
   [:lu
    (for [[index r] (map-indexed vector replacements)]
     [:li {:id index :classes ["replacement"]} r]
      )]])

(defn grammar-error [text tooltip]
  [:span {:classes ["grammar-checker-problem"]} text])

(defn apply-replacement-fn [mark]
  (fn [replacement]
    (let [{from "from" to "to"} (js->clj (cm/find-mark mark))]
      (cm/replace-range @editor replacement from to))))

(defn attach-popover [element replacements mark]
  (listen! element :mouseover #(do (.popover (js/jQuery element) "show")
                                 (let [popover (sel1 :.popover)
                                       items (sel popover :.replacement)
                                       f (apply-replacement-fn mark)]
                                   (doseq [item items]
                                     (let [id (.-id item)]
                                       (listen! item :click (fn [e] 
                                                              (f (get replacements id))
                                                              (.popover (js/jQuery element) "hide")))))
                                   (listen! popover :mouseout (fn [e] (do (when-not (descendant? (.-relatedTarget e) popover) 
                                                                            (.popover (js/jQuery element) "hide"))))))))
  mark)

(defn handle-grammar-check-result [coll]
  (replace-contents! (sel1 :#check-grammar-result)
                     (template/node
                        [:div
                          [:tr  [:td "##"] [:td [:b "Description"]]]
                          [:tbody (map (fn [r] [:tr [:td (:line r)] [:td (:message r)]]) coll)]]))
  
  (doseq [mark @grammar-check-marks] (cm/clear-mark mark))
  (reset! grammar-check-marks [])
  
  ;(.setGutterMarker @editor 1 "grammar-checker-problem-gutter" (template/node [:span "x"]))
  
  (doseq [r coll]
    (let [{:keys [line column end-line end-column message suggested-replacements]} r
          from {:line line :ch (dec column)}
          to {:line end-line :ch (dec end-column)}
          text (cm/get-range @editor from to)
          tooltip (template/node (grammar-error-tooltip message suggested-replacements))
          element (template/node (grammar-error text tooltip))]
      (.popover (js/jQuery element) (clj->js {:container "body"
                                              :title message 
                                              :content (.-outerHTML tooltip)
                                              :trigger "manual"
                                              :html true
                                              :placement "bottom"}))
      
      (->> (cm/mark-text @editor from to
                         {:clearOnEnter true
                          :replacedWith element
                          })
           (attach-popover element suggested-replacements)
           (swap! grammar-check-marks conj)
           )
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