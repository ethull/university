import Base
import System.IO

-- cartina (start room) -> kitchen (goal room)
--    | 
-- trollDen

opposite :: Direction -> Direction
opposite North = South
opposite South = North
opposite East = West
opposite West = East

-- repersent lack of actions in a room
noActions :: Item -> GameState -> Next GameState
noActions i (GS p r) = Same $ "in " ++ name r ++ ", a cant do anything with: " ++ show i

--kitchen2 = Room { name = "kitchen", description = "The beginner room", isWinRoom = True,   actions = noActions }
-- name, description, isWinRoom, requires, items, monsters, doors, actions
kitchen = Room "kitchen" "the goal room" True (Just Key) [] [] [] noActions

cartina = Room "cartina" "the starting room" False Nothing [(Spoon, "the spoon of death")] [] [(East, kitchen), (South, trollDen)] noActions

troll = WoodTroll 10 Key
trollDen = Room "trollden" "room with big troll" False Nothing [] [troll] [] action
action :: Item -> GameState -> Next GameState
action Spoon (GS p r) 
  | length (monsters r) > 0 = 
    --if health troll > 5 
    let crtMonst = head (monsters r) in
      if health crtMonst > 5  -- head instead of !! 0
         then Progress "monster was attacked but seems all good in the hood" $ GS p r { monsters = [crtMonst { health = 5 }] }
         else Progress "monster was killed" $ GS p r { monsters = [], items = [(holding crtMonst, "a key")] }
  | otherwise = Same $ "in room " ++ name r ++ ", there are no monsters"

player1 = Player "bob" []
gameO = GS { player = player1, room = cartina }


instance Parsable Item where
  parse "key" = Just Key
  parse "spoon" = Just Spoon
  parse _ = Nothing

instance Parsable Direction where
  parse "north" = Just North
  parse "south" = Just South
  parse "east"  = Just East 
  parse "west"  = Just West 
  parse _ = Nothing

instance Parsable Command where
  parse ('g':'o':' ':x) = case parse x of
                            Nothing -> Nothing
                            Just n -> Just (Move n)
  parse ('g':'r':'a':'b':' ':x) = case parse x of
                                    Nothing -> Nothing
                                    Just n -> Just (PickUp n)
  parse ('u':'s':'e':' ':x) = case parse x of
                                Nothing -> Nothing
                                Just n -> Just (Use n)
  -- help menu, user may or may not provide an argument
  parse ('h':'e':'l':'p':' ':x) = case parse x of
                                Nothing -> Just (Help GeneralHelp)
                                Just n -> Just (Help n)
  parse "help" = Just (Help GeneralHelp)

  parse "end" = Just End
  parse _ = Nothing

tellResponse :: String -> IO ()
tellResponse s = putStrLn $ "< " ++ s ++ "."

readCommand :: IO (Maybe Command)
readCommand = putStr "> " >> hFlush stdout  >> (getLine >>= return . parse)


deleteFrom :: Eq a => a -> [(a, b)] -> [(a, b)]
deleteFrom a [] = []
deleteFrom a mp = filter (\x -> fst x /= a) mp

leaveRoom :: Room -> Direction -> Room -> Room
leaveRoom fromRoom dir toRoom = toRoom { doors = (opposite dir, fromRoom) : deleteFrom dir (doors toRoom) }

step :: Command -> GameState -> Next GameState
step (Move d) (GS p r) = 
  case lookup d (doors r) of
   Nothing -> Same $ "there is no room in dir " ++ show d
   Just n -> case requires n of
               Nothing -> Progress ("success, moved to room " ++ name n) (GS p (leaveRoom r d n))
               Just m -> if elem m (inventory p) then Progress ("success, moved to room " ++ name n) (GS p (leaveRoom r d n))
                         else Same $ "dont have required item "  ++ show m ++ " for room " ++ name n
step (PickUp i) (GS p r) = case lookup i (items r) of
   Nothing -> Same $ "there is nothing to pickup in room " ++ name r
   Just n -> Progress ("item " ++ show n ++ " successfully picked up") 
    (GS p { inventory = i : inventory p } r { items = deleteFrom i (items r)})
step (Use i) (GS p r) = if elem i (inventory p) then actions r i (GS p r)
                        else Same $ "player doesnt have item " ++ show i

play :: GameState -> IO ()
play (GS p r) = tellContext (GS p r) >> playLoop (GS p r)
playLoop :: GameState -> IO ()
playLoop (GS p r) | isWinRoom r = putStrLn $ "player " ++ playerName p ++ " has won"
  | otherwise = readCommand >>= 
    \inp -> case inp of
      Nothing -> putStrLn "entered command is invalid" >> playLoop (GS p r)
      Just End -> putStrLn "game over GG" >>= return
      Just (Help n) -> tellHelp n >> playLoop (GS p r)
      Just n -> case step n (GS p r) of
                  Same m -> tellResponse m >> playLoop (GS p r)
                  Progress m (GS np nr) -> tellResponse m >> tellContext (GS np nr) >> playLoop (GS np nr)

main :: IO ()
main = play gameO


tellHelp :: HelpOption -> IO ()
tellHelp MoveHelp = putStrLn "go x: move to room x"
tellHelp PickUpHelp = putStrLn "grab x: pickup item x"
tellHelp UseHelp = putStrLn "use x: use item x"
tellHelp EndHelp =  putStrLn "end: quit the current game"
tellHelp HelpHelp =  putStrLn "help x: get help on command x"
tellHelp GeneralHelp = do {
                      putStrLn "go x: move to room x";
                      putStrLn "grab x: pickup item x";
                      putStrLn "use x: use item x";
                      putStrLn "end: quit the current game";
                      putStrLn "help x: get help on command x";
                  }

instance Parsable HelpOption where
-- user can enter anything after help as long as a recognised command is entered first
  parse ('g':'o':x) = Just MoveHelp
  parse ('g':'r':'a':'b':x) = Just PickUpHelp
  parse ('u':'s':'e':x) = Just UseHelp
  parse ('h':'e':'l':'p':x) = Just HelpHelp
  parse "end" = Just EndHelp
  parse _ = Nothing

