#!/usr/bin/env bash
#
# Verifica as regras invioláveis do .cursorrules que dão para checar estaticamente.
# Roda no CI e localmente:  bash .github/scripts/contract-lint.sh
#
set -uo pipefail

SRC="${CONTRACT_SRC:-src/test/java/com/igorroberth/swaglabs}"
failures=0

# Zera comentários de linha inteira, preservando a numeração, para que uma regra
# citada em comentário não vire falso positivo.
#
# Só remove comentário que ocupa a linha toda. Um `//` no meio da linha fica: tentar
# recortá-lo apagaria XPath dentro de string literal ("//div[...]"), que é justamente
# uma das violações a detectar. O custo é que um comentário no fim de uma linha de
# código pode gerar falso positivo — escreva-o na linha de cima.
strip_comments() {
    sed -e 's|^[[:space:]]*//.*||' \
        -e 's|^[[:space:]]*\*.*||' \
        -e 's|^[[:space:]]*/\*.*||' \
        "$1"
}

scan() {
    local pattern="$1"
    shift
    local dir file
    for dir in "$@"; do
        [ -d "$SRC/$dir" ] || continue
        while IFS= read -r file; do
            strip_comments "$file" | grep -nE "$pattern" | sed "s|^|${file#"$SRC"/}:|"
        done < <(find "$SRC/$dir" -name '*.java' | sort)
    done
}

check() {
    local label="$1" result="$2"
    if [ -n "$result" ]; then
        printf 'FALHOU  %s\n' "$label"
        printf '%s\n' "$result" | sed 's/^/          /'
        failures=$((failures + 1))
    else
        printf 'ok      %s\n' "$label"
    fi
}

missing_test_displayname() {
    local file
    while IFS= read -r file; do
        awk -v file="${file#"$SRC"/}" '
            /@Test/ { pending = NR; next }
            pending && NF {
                if ($0 !~ /@DisplayName/) print file ":" pending ": @Test sem @DisplayName"
                pending = 0
            }
        ' "$file"
    done < <(find "$SRC/tests" -name '*.java' | sort)
}

missing_class_displayname() {
    local file
    while IFS= read -r file; do
        awk -v file="${file#"$SRC"/}" '
            /^(public )?(final )?class / {
                if (previous !~ /@DisplayName/) print file ":" NR ": classe de teste sem @DisplayName"
            }
            { previous = $0 }
        ' "$file"
    done < <(find "$SRC/tests" -name '*.java' | sort)
}

long_methods() {
    local file
    while IFS= read -r file; do
        awk -v file="${file#"$SRC"/}" '
            /^    (public|private|protected).*\(.*\) \{/ { start = NR; count = 0; inside = 1 }
            inside { count++ }
            /^    \}$/ {
                if (inside && count > 20) print file ":" start ": método com " count " linhas (limite 20)"
                inside = 0
            }
        ' "$file"
    done < <(find "$SRC" -name '*.java' | sort)
}

echo "Contrato de arquitetura — .cursorrules"
echo

check "1. Nenhum seletor fora de pages/ e components/" \
      "$(scan 'getByTestId\(|getByRole\(|getByLabel\(|getByPlaceholder\(|getByText\(|\.locator\(' tests data support)"

check "2. Nenhuma asserção dentro de pages/ e components/" \
      "$(scan 'assertThat\(|Assertions\.|assertEquals|assertTrue|assertFalse' pages components)"

check "3. Nenhuma espera manual" \
      "$(scan 'Thread\.sleep|waitForTimeout\(|waitForSelector\(|\.waitFor\(' tests data support pages components)"

check "4. Nenhuma leitura de String do DOM" \
      "$(scan '\.textContent\(\)|\.innerText\(\)|\.inputValue\(\)|\.allTextContents\(\)|\.getAttribute\(' tests data support pages components)"

check "5. components/ não conhece pages/" \
      "$(scan 'swaglabs\.pages' components)"

check "6. Nenhuma lógica dentro de tests/" \
      "$(scan '^[[:space:]]+(if|for|while|switch) \(|^[[:space:]]+try \{' tests)"

check "7. Nenhum teste dependente de ordem" \
      "$(scan '@([A-Za-z_]+\.)*Order\b|@([A-Za-z_]+\.)*TestMethodOrder\b|MethodOrderer' tests)"

check "8. Nenhum XPath" \
      "$(scan 'xpath=|locator\("//' tests data support pages components)"

check "9. Nenhuma saída em stdout" \
      "$(scan 'System\.out|System\.err|printStackTrace' tests data support pages components)"

check "10. @DisplayName em todo método de teste" "$(missing_test_displayname)"

check "11. @DisplayName em toda classe de teste" "$(missing_class_displayname)"

check "12. Nenhum método acima de 20 linhas" "$(long_methods)"

echo
if [ "$failures" -gt 0 ]; then
    echo "$failures regra(s) violada(s)."
    exit 1
fi
echo "Contrato íntegro."
