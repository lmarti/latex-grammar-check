(ns latex-grammar-check.latex-test
  (:require [clojure.test :refer :all]
            [latex-grammar-check.latex :as latex]))

(deftest home-page-test
  (is (= [{:node-type :tword, :text "word1", :line 1, :pos 1} 
          {:node-type :twhitespace, :text " ", :line 1, :pos 6} 
          {:node-type :tword, :text "word2", :line 1, :pos 7}]
         (latex/tokenize "word1 word2"))))

(run-tests)

;(println (latex/extract-text (latex/tokenize (slurp "src/sablecc/sample.tex"))))

;(latex/extract-text "word1\n\\section{Ordinary Text}\nword2")

;(latex/extract-text "word1 word2
;                    \\section{Ordinary Text}
;                     word3 word4\n
;                    % sfff")
