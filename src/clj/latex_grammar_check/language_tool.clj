(ns latex-grammar-check.language-tool
  (:import [org.languagetool JLanguageTool]
           [org.languagetool.language BritishEnglish]))

;tbd: rename namespace to language-tool
(defn check-grammar [text]
  (let [lang-tool (JLanguageTool. (BritishEnglish.))]
    (.activateDefaultPatternRules lang-tool)
    (.disableRule lang-tool "WHITESPACE_RULE")
    (let [matches (.check lang-tool text)]
      (map (fn [m] {:line (.getLine m)
                    :end-line (.getEndLine m)
                    :column (dec (.getColumn m))
                    :end-column (dec (.getEndColumn m))
                    :message (.getMessage m)
                    :rule-id (.. m getRule getId)
                    :replacements (vec (.getSuggestedReplacements m))}) matches))))

;(check-grammar "A sentence with a error in the Hitchhiker's Guide tot he Galaxy")

;(check-grammar "The blue      sea")
