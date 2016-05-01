$(shell \
	printf '' >gitinfo.gen.tex.new ;\
	echo '\newcommand{\gitunclean}{$(shell [ -n "$$(git status --porcelain)" ] && echo " (WARNING: MODIFIED SINCE!)" || echo "")}' >>gitinfo.gen.tex.new ;\
	echo '\newcommand{\gitrev}{$(shell git rev-parse HEAD)}' >>gitinfo.gen.tex.new ;\
	echo '\newcommand{\gitshortrev}{$(shell git rev-parse --short HEAD)}' >>gitinfo.gen.tex.new ;\
	echo '\newcommand{\gitauthordate}{$(shell git show -s --format=%ai HEAD)}' >>gitinfo.gen.tex.new ;\
	echo '\newcommand{\giturl}{https://github.com/michalrus/agh-semmaps/commit/$(shell git rev-parse HEAD)}' >>gitinfo.gen.tex.new ;\
  diff gitinfo.gen.tex gitinfo.gen.tex.new >/dev/null 2>&1 && rm gitinfo.gen.tex.new || mv gitinfo.gen.tex.new gitinfo.gen.tex)

.PHONY: all view deploy stats

all: main.pdf defense-pl/main.pdf

view: all
	xdg-open $<

TEX_T = $(shell find . -name '*.tex' -a '!' -name '*.gen.tex' -a '!' -path '* *' -a '!' -path './defense-pl/*')

FIG_T = $(shell find . '(' -name '*.pdf' -o -name '*.png' -o -name '*.jpg' ')' -a '!' '(' -name 'main.pdf' -o -name '* *' -o -path './defense-pl/*' ')')

TEX_D = $(shell find ./defense-pl -name '*.tex' -a '!' -name '*.gen.tex' -a '!' -path '* *')

FIG_D = $(shell find ./defense-pl '(' -name '*.pdf' -o -name '*.png' -o -name '*.jpg' ')' -a '!' '(' -name 'main.pdf' -o -name '* *' ')')

main.pdf: gitinfo.gen.tex $(TEX_T) $(FIG_T)
	latexmk -f -pdf -interaction=nonstopmode main.tex

defense-pl/main.pdf: gitinfo.gen.tex $(TEX_D) $(FIG_D)
	cd defense-pl && latexmk -f -xelatex -shell-escape -interaction=nonstopmode main.tex

defense-pl/main.tex: defense-pl/beamerthemem.sty defense-pl/beamerfontthememetropolis.sty defense-pl/beamercolorthememetropolis.sty defense-pl/FiraMono-Regular.ttf defense-pl/FiraSans-Light.ttf defense-pl/FiraSans-Regular.ttf defense-pl/FiraSans-LightItalic.ttf defense-pl/FiraSans-Italic.ttf defense-pl/FiraSans-ExtraLight.ttf defense-pl/FiraSans-Medium.ttf

defense-pl/%.sty:
	cd defense-pl && wget https://raw.githubusercontent.com/matze/mtheme/a54d7477a9d52a8ef62536c1faaa0dbf75e85b81/$(notdir $@)
	sed -re 's/\{Fira ?Sans Italic\}/\{FiraSans-Italic\}/g' -i $@ ;\
	sed -re 's/\{Fira ?Sans Light Italic\}/\{FiraSans-LightItalic\}/g' -i $@ ;\
	sed -re 's/\{Fira ?Sans\}/\{FiraSans-Regular\}/g' -i $@ ;\
	sed -re 's/\{Fira ?Sans Light\}/\{FiraSans-Light\}/g' -i $@ ;\
	sed -re 's/\{Fira ?Sans Medium\}/\{FiraSans-Medium\}/g' -i $@ ;\
	sed -re 's/\{Fira ?Sans ExtraLight\}/\{FiraSans-ExtraLight\}/g' -i $@ ;\
	sed -re 's/\{Fira( ?Sans)? Mono\}/\{FiraMono-Regular\}/g' -i $@ ;\
	sed -re 's/\\setsansfont\[/\\setsansfont[Path=.\/, /g' -i $@ ;\
	sed -re 's/\\setmonofont\{/\\setmonofont[Path=.\/]{/g' -i $@ ;\
	sed -re 's/\\newfontfamily\\([^\{]+)\{/\\newfontfamily\\\1[Path=.\/]{/g' -i $@

defense-pl/%.ttf:
	cd defense-pl && wget https://raw.githubusercontent.com/mozilla/Fira/73645a967e9bfc8fc9c67d630cff517e781a0590/ttf/$(notdir $@)

deploy: main.pdf
	@test -z "$$(git status --porcelain)" || { echo >&2 'fatal: unclean repository state, aborting.' ; exit 1 ; }
	echo 'put main.pdf /michalrus.com/cv/thesis-msc.pdf' | sftp michalrus@michalrus.com
