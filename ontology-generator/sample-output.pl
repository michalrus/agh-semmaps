%% Example of other classes, which may be more compatible with students approach(POI, obstacle)
%% class(poi).
%% subclass(static_poi, poi).
%% subclass(dynamic_poi, poi).
%% subclass(door, static_poi).
%% class(area).
%% subclass(hallway, area).
%% subclass(classroom, area).
%% subclass(toilet, area).
%% subclass(staircase, area).

%% area{at1:val1, ...} has [ 
%% 	door{at1:val1, ...},
%% 	...
%% ].


%% kazde poi i obstacle powinno byc klasa 
class(place).
class(classroom).
class(hallway).
class(tv).
class(door).
class(desk).
class(closet).
class(whiteboard).
class(window).
class(projector).
class(computer).
class(sink).

%te relacje nalezy zostawic -  nie dodawac nowych
relation(has).
relation(consists_of).

subclass(classroom, place).
subclass(hallway, place).


classroom{id:lab316, name:"Laboratorium 316", size:small, color:yellow} has [
	tv{color:black, model:samsung},
	door{color:gray},
	desk{gid:lab316_desk} * 16,
	closet{color:brown} * 3,
	whiteboard{model:smartboard},
	whiteboard{},
	window{} * 2,
	projector{color:white},
	computer{color:black, model:hp} * 6
].

classroom{id:brokenmirror316, size:small, color:yellow{intensity:dark}} has [
    tv{color:black, model:samsung} near [window{color:red}],
    door{color:gray},
    desk{color:brown{intensity:high}, model:regular} * 16,
    closet{color:brown} * 3,
    whiteboard{model:smartboard},
    whiteboard{},
    window{} * 2,
    projector{color:red},
    computer{color:black,model:acer} * 6,
    sink{}
].
