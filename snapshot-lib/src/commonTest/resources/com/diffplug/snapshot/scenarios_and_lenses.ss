╔═ test ═╗
╔═ test/scenario ═╗
╔═ test/scenario/subscenario ═╗
No limit to how deep scenario nesting can go.

╔═ test[lens pretty-print] ═╗
╔═ test[lens only plaintext] ═╗
Tests, scenarios, and lenses have no length or character restrictions.
We escape the following:
 - \ -> \\
 - / -> \∕  (mathematical division slash)
 - [ -> \(
 - ] -> \)
 - newline -> \n
 - tab -> \t
 - ╔ -> \┌
 - ╗ -> \┐
 - ═ -> \─
╔═ test with \∕slash\∕ in name ═╗
╔═ test with \(square brackets\) in name ═╗
╔═ test with \\backslash\\ in name ═╗
╔═ test with \nnewline\n in name ═╗
╔═ test with \ttab\t in name ═╗
╔═ test with \┌\─ ascii art \┐\─ in name ═╗