# TO DO

- move popover related code to popover.cljs
- use tooltip where there are no recommendations
- fix \documentstyle[html]{article} bug
   general problem of not including some text between {}
- Not checking of heading text: \title \author \inst \institute \part \chapter \section \subsection \subsubsection \paragraph. 
  Users should be able to enable/disable this add more tags.
- fucking improve the stucture of the code
- "check in progress" sign (hourglass, spinning thingy, etc.)
- also display error message and recommendations on the gutter
- message "check passed successfully"
- drop down for language selection.
- \footnote{} should be treated as an independent sentence from its context.
- convert -, -- and --- to - or use they utf8 counterparts. 
- How to treat inline equations: $x_i$ to the power of $\hat{y}$ equals $0$.
- How to treat \cite{}
- How to treat \ref{}
- How to treat \newcommand{}s
- Error feedback when the .tex file is not a valid latex for the parser.
- support to Ignore error / Ignore all errors of this type
- use jayq instead of using jquery directly


# Main use case

- A user pastes or uploads a .tex file.
- Its shown in a text area and automatically checked. 
- Errors are marked in the left of the text area an listed bellow. 
- The user can edit the text.
- When there are no errors a red light turns green.
- The user can download the file or just copy it.

# Long term features

- Have users, with workspaces.
- Integration with dropbox and google docs.
- Possible integration with writelatex.com or sharelatex.com.
 
# DONE

## April 20, 2013
- correct show/hide of popover
- display error message and recommendations on mouse over

## April 13, 2013

- deal with column number issue
- get language-tool to ignore this error "Possible typo: you repeated a whitespace"  (the langtool api supports this)

## April 15, 2013

- clear existing marks before adding new ones
