(ns latex-grammar-check.client.codemirror)

(defn create-editor [text-area opts]
  (.fromTextArea js/CodeMirror text-area (clj->js opts)))

(defn get-value 
  ([editor] (.getValue editor))
  ([editor separator] (.getValue editor separator)))

(defn set-value [editor value] 
  (.setValue editor value))

(defn focus [editor] 
  (.focus editor))

(defn mark-text [editor from to options] 
  (.markText editor
            (clj->js from)
            (clj->js to)
            (clj->js options)))

(defn clear-mark [mark]
  (.clear mark))

(defn find-mark [mark]
  (.find mark))

(defn get-range [editor from to]
  (.getRange editor (clj->js from) (clj->js to)))

(defn replace-range [editor replacement from to]
  (.replaceRange editor replacement (clj->js from) (clj->js to)))

; tbd: add to externs
(defn on [target event-type handler]
  (case event-type
    :clear (.on js/CodeMirror target (name event-type) handler)
    "tbd: throw exception"))

; tbd: add to externs
(defn off [target event-type handler]
  (case event-type
    :clear (.off js/CodeMirror target (name event-type) handler)
    "tbd: throw exception"))


