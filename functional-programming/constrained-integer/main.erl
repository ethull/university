-module(main).
-compile(export_all).

% c(main).
% main:set_up([x,y,z],2,[{x,y},{y,z}]).
% main:report([{x,<0.xpid.0>},{y,<0.xpid+1.0>},{z,<0.xpid+2.0>}]).
% main:report([{x,<0.107.0>},{y,<0.108.0>},{z,<0.109.0>}]).

% check if key V exists in association list XS
pick([], _) -> error;
pick([{K, W}|XS], V) -> 
    if K==V -> W;
       true -> pick(XS, V) end.

send_all([], _) ->ok;
send_all([H|T], M) -> H!M, send_all(T, M).

% atomforks: [atoms], returns [{atom, cipid}]
atomforks([], _) -> [];
atomforks([H|T], F) -> [{H, spawn(F)}|atomforks(T, F)].
%test mtd to output CI pids to test the report mtd
%atomforks([H|T], F) -> X={H, spawn(F)}, io:format("atf: ~p~n", [X]), [X|atomforks(T, F)].

% assume soundness on call
constrained_integer(UB, L) ->
    receive
        % this ci telling anouther ci what its UB is
        {upperbound, Pid} -> Pid!{upperbound,UB,self()}, NUB=UB, NL=L;
        % being told by a ci with this ci in its LB, that its UB has changed
        {impose, UB2} -> 
            % our UB should be < the senders UB, if not then change and inform LBs
            if UB > UB2 -> NUB=UB2, send_all(L, {impose, NUB-1});
            true -> NUB=UB
            end, NL=L;
        {lowerbound, Pid} -> 
            NL=[Pid|L], Pid!{upperbound, self()},
            receive {upperbound,UB2,Pid} ->
                        %if UB2 > UB -> UB=UB2+1, send_all(L, {impose, UB}) end
                        if UB2 >= UB -> send_all(NL, {impose, UB-1}) end, NUB=UB
            end
    end,
    %io:format("self, pUB, cUB, pL, cL: ~p ~p ~p ~p ~p ~n",[self(), UB, NUB, L, NL]),
    constrained_integer(NUB, NL).

get_upper(Pid) -> Pid!{upperbound,self()},
                  receive {upperbound, UB, Pid} -> UB end.

% process_constraints: [{cx,cy}] (cx<cy), [{atom, cipid}], for each {cx,cy} tell cy cipid cx cipid is its LB
process_constraints([],_) ->ok;
process_constraints([{P,Q}|LC],AL) -> pick(AL,Q)!{lowerbound, pick(AL,P)}, process_constraints(LC,AL).

% set_up: [atoms], globalUpperBound, [{cx,cy}], return [{atom, cipid}]
% process)constraints deals with the LB, and atomforks deals with the UB
set_up(LA, N, LC) -> process_constraints(LC, atomforks(LA, fun()->constrained_integer(N, []) end)).

% report: [{atom, cipid}], return [{atom, UB}]
report([]) -> [];
report([{A, Pid}|T]) -> [{A, get_upper(Pid)}|report(T)].
