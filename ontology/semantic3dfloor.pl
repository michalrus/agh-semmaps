:-dynamic(xtype/1).
:-dynamic(xattr/1).
:-dynamic(schema/1).
:-dynamic(entry/4).
:-dynamic(itemset/1).
:-dynamic(xtemset/2).
:-dynamic(arff_record/1).
:-dynamic(arff_attribute/2).

:-op(300,xfy,has).
:-op(830,fx,xtype).         % used to define type
:-op(830,fx,xattr).         % used to define attribute
:-op(830,fx,schema).         % used to define attribute
:-op(816,xfx,to).           % used to specify ranges 


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
xtype [
  name: rooms,
  base: symbolic,
  domain : [room316, corridor, room317, room318, room319]
].

xtype [
  name: objects,
  base: symbolic,
  countable: yes,
  domain : [tv, desk, closet, whiteboard, window, projector]
].

xtype[
  name: colors,
  base: symbolic,
  domain: [red, black, gree, white]
].

xtype[
  name: models,
  base: symbolic,
  domain: [samsung, smartboard]
].



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

xattr [
  name: room,
  type: rooms, 
  class: simple, 
  comm: in
].

xattr [
  name: object,
  type: objects,
  class: simple, 
  comm: in,
  askable: 10

].


xattr [
  name: color,
  type: colors,
  class: general, 
  comm: in,
  askable: 50
].

xattr [
  name: model,
  type: models,
  class: simple, 
  comm: in,
  askable: 100
].

%%%%%%%%%%%%%%%%%%%%% HIERARCHY %%%%%%%%%%%%%%%%%%%%%%%%%%%

%SCHEMA xattr schema_has xattr
schema [
    room has object,
    object has color,
    object has model,
    color has intensity
].

% TODO: smartboard is whiteboard how to cope with that?


%%%%%%%%%%%%%%%%%%%%% INSTANCES %%%%%%%%%%%%%%%%%%%%%%%%%%%

room(lab316) has  [
     [object(tv) has 
                        [ 
                           color(black), 
                           model(samsung)
                        ]
    ],
    [object(door)   has  [ color(grey)]],
    [object(desk)   has  [ color(brown), count(16)]], %count defines how many 
                                                      %exaclty the same instances 
                                                      %are present. Default 1.
    [object(closet) has  [color(brown), count(3)]],
    [object(whiteboard) has [model(smartboard), count(1)]],
    [object(whiteboard) has [count(1)]],
    [object(window) has [count(2)]],
    [object(projector) has [color(white)]],
    [object(computer) has [color(black), model(hp), count(6)]]
    
].


room(brokenmirror316) has [
    [object(tv) has 
                        [ 
                           color(black), 
                           model(samsung)
                        ]
    ],
    [object(door)   has  [ color(grey)]],
    [object(desk)   has  [ [color(brown) has [intensity(high)]], count(16),model(regular)]],
    [object(closet) has  [color(brown), count(3)]],
    [object(whiteboard) has [model(smartboard), count(1)]],
    [object(whiteboard) has [count(1)]],
    [object(window) has [count(2)]],
    [object(projector) has [color(red)]],
    [object(computer) has [color(black), model(acer), count(6)]],
    object(sink)
].

%%%%%%%%%%%%%%%%%%%%% ALGORITHMS %%%%%%%%%%%%%%%%%%%%%%%%%%%

%get_rules([[object,tv]],[lab316,lab318],model,colorCertain,Ambiguous)
%get_rules([],[lab316,lab318],object,Certain,Ambiguous)
%get_rules(ValuesAssumed, Classes,ForProperty,CertainRules, AmbiguousRules) :-
 %-> filter all data according to classes
 %-> filter data according to assumedValues
 %-> filter data acording to ForProperty
 %-> generate rules






%%%%%%%%%%%%%%%%%%%%% TOOLS %%%%%%%%%%%%%%%%%%%%%%%%%%%

find_root(Root,Id, Content):-
  schema (Schema), 
  member((Root has _),Schema), 
  \+member((_ has Root),Schema),
  A has Content,
  A =..[Root, Id].
  

%get_property(Element, Properties) :-
%  schema (Schema), 
%  findall(Property,member((Element has Property),Schema), Properties).


%get_value([[object, tv]], model, lab316, Value)!!!!TODO
%get_value(OfProperty,  ForRootValue , Value) :-
%  find_root(_, ForRootValue, Content), 
%  member(Goal, Content),
%  Goal =.. [OfProperty,Value].

%get_value(OfProperty,  ForRootValue , Value) :-
%  find_root(_, ForRootValue, Content),
%  member([Property has _], Content), 
%  Property =.. [OfProperty, Value].

%%%%%%%%%%%%%%%%%%%%% WEKA ATTEMPT %%%%%%%%%%%%%%%%%%%%%%%%%%%
to_arff(_,_) :-
  retractall(entry(_,_,_,_)),
  retractall(arff_record(_)),
  retractall(arff_attribute(_,_)),
  find_root(_, Class, Content),
  process_elements(Content, '', Class,[]), 
  fail.

to_arff(RelName,Filename) :- 
  findall(Label, (entry(Label, _,_,_),Label\=break),Labels), 
  list_to_set(Labels, SetLabels), 
  findall(Class, find_root(_,Class,_), Classes),
  entry_to_record(Records),!,
  assert_arff_records(SetLabels,Classes),
  assert_arff_header(SetLabels,Records),
  write_arff(RelName, Filename).


assert_arff_header([],_).
assert_arff_header([Attribute|SetLabels],Records) :- 
  findall(Value,
    (
      member([SingleRecord,_],Records),
      member([Attribute,Value],SingleRecord)
    ),Domain),
  list_to_set(Domain,UniqueDomain),
  assert(arff_attribute(Attribute,UniqueDomain)),
  assert_arff_header(SetLabels,Records).


write_arff(RelName, Filename) :- 
  tell(Filename),
    write('@relation '),write(RelName),nl,nl,
    print_header,
    print_class_header,nl,
    write('@data'),nl,
    print_data,
  told.

print_header :- 
  arff_attribute(Name, Domain),
  write('@attribute '), write(Name), write(' {'),
  print_header_helper(Domain),
  write('}'),nl,
  fail.

print_header.

print_header_helper([Last|[]]) :- !,
  write(Last).
print_header_helper([De|[Next|Domain]]) :-
  write(De),write(', '),
  print_header_helper([Next|Domain]).

print_class_header :-
  findall(Class, find_root(_,Class,_),Classes), 
  find_root(ClassName,_,_),
  write('@attribute ' ),write(ClassName), write(' {'),
  print_class_header_helper(Classes),
  write('}'),nl.

print_class_header_helper([Last|[]]) :-
  write(Last).
 
print_class_header_helper([C|[Next|Classes]]) :-
  write(C),write(','),
  print_class_header_helper([Next|Classes]).


print_data :-
  arff_record(Record),
  print_data_helper(Record),
  fail.

print_data.

print_data_helper([Class|[]]) :- !,
  write(Class),nl.
 
print_data_helper([[]|[Next|Data]]) :- !,
  write('?,'),
  print_data_helper([Next|Data]).


print_data_helper([De|[Next|Data]]) :- !,
  write(De),write(', '),
  print_data_helper([Next|Data]).



process_elements([],_,_,_).
process_elements([Element|Rest],ParentPrefix,Class,Parent) :-  
   process_element(Element,ParentPrefix,Class,Parent), 
   process_elements(Rest,ParentPrefix,Class,Parent),
   (Parent = [],!,assertz(entry(break,break,Class,break)));true.


process_element(Element,ParentPrefix,Class,Parent) :-
  Element  = [_|_],
  process_elements(Element,ParentPrefix,Class,Parent).

process_element(Element,ParentPrefix,Class,Parent):- 
  Element = Item has Property,
  Item =.. [Name, Val],
  atom_string(Val, StringVal), 
  string_concat(ParentPrefix, StringVal, NewParentPrefix), 
  merge(Parent,[[Name, Val]], NewParent),
  process_element(Item,ParentPrefix,Class,Parent),
  process_elements(Property,NewParentPrefix,Class,NewParent).

process_element(Element,ParentPrefix,Class,Parent):- !,
  Element =.. [Name, Value],
  atom_string(Name, StringVal), 
  string_concat(ParentPrefix, StringVal, Label), 
  merge(Parent,[[Label, Value]], NewParent),
  assertz(entry(Label,Value, Class,NewParent)).
 


assert_arff_records(_,[]).
assert_arff_records(Labels, [C|Classes]) :-
  assert_arff_records_helper(Labels, C),
  assert_arff_records(Labels, Classes).

assert_arff_records_helper(Labels, Class) :-
  xtemset(Data,Class),
  prepare_arff_record(Labels,Data,Class,Record),
  retract(xtemset(Data,Class)),
  assert(arff_record(Record)),
  fail.

assert_arff_records_helper(_,_).


prepare_arff_record([],_,Class,[Class]) :- !.
prepare_arff_record([L|Labels], Data,Class,[Value|Rest]) :-
  member([L,Value],Data),
  prepare_arff_record(Labels,Data,Class,Rest).

prepare_arff_record([L|Labels], Data,Class,[[]|Rest]) :-
  \+member([L,_],Data),
  prepare_arff_record(Labels,Data,Class,Rest).



itemset([]).

entry_to_record(Records):-
  findall([L,V,C],entry(L,V,C,_), List),
  entry_to_record_h(List,Records).

entry_to_record_h([],[]):-!.
entry_to_record_h([[L,V,_]|List],Records):-
  L\=break,
  itemset(A),
  retractall(itemset(A)),
  merge(A,[[L,V]],R),
  asserta(itemset(R)),!,
  entry_to_record_h(List,Records).

entry_to_record_h([[L,_,C]|List],[[A,C]|Records]):- 
  L=break,
  itemset(A), 
  A\=[],
  asserta(xtemset(A,C)),
  retractall(itemset(A)),
  asserta(itemset([])),!,
  entry_to_record_h(List,Records).

entry_to_record_h([_|List],Records) :- !,
  entry_to_record_h(List,Records).







