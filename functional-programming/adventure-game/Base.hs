module Base where

import Data.List

data GameState = GS { player :: Player, room :: Room }

data Next a =
      Same String
    | Progress String a

data Player =
  Player
    {   playerName :: String
      , inventory  :: [Item]
    }

data Room =
  Room
    {  name        :: String
     , description :: String
     , isWinRoom   :: Bool
     , requires    :: Maybe Item
     , items       :: [(Item, String)]
     , monsters    :: [Monster]
     , doors       :: [(Direction, Room)]
     , actions     :: Item -> GameState -> Next GameState
    }

data Direction = North | South | East | West
  deriving Eq

instance Show Direction where
  show North  = "north"
  show South  = "south"
  show East   = "east"
  show West   = "west"

data Item = Key | Spoon
  deriving Eq

instance Show Item where
  show Key   = "key"
  show Spoon = "spoon"

data Monster = WoodTroll { health :: Int, holding :: Item }

instance Show Monster where
  show (WoodTroll health item) = "wood troll holding a " ++ show item


data Command =
  Move Direction | Use Item | PickUp Item | Help HelpOption | End
data HelpOption = 
  MoveHelp | UseHelp | PickUpHelp | EndHelp | HelpHelp | GeneralHelp


class Parsable t where
  parse :: String -> Maybe t


tellContextLine :: String -> IO ()
tellContextLine s = putStrLn $ "   " ++ s ++ "."

tellDoors :: [(Direction, Room)] -> IO ()
tellDoors [] = tellContextLine "There are no doors."
tellDoors [(dir, _)] = tellContextLine $ "There is a door to the " ++ show dir
tellDoors doors =
  tellContextLine $ "There are doors to the " ++ intercalate " and " (map (show . fst) doors)

tellItem :: (Item, String) -> IO ()
tellItem (item, pos) = tellContextLine $ pos ++ " there is a " ++ show item

tellMonster :: Monster -> IO ()
tellMonster monster = tellContextLine $ "There is a " ++ show monster

tellContext :: GameState -> IO ()
tellContext (GS p r) = do
  putStrLn ""
  tellContextLine $ "You are in a " ++ name r ++ ". It is " ++ description r
  tellDoors (doors r)
  mapM tellItem (items r)
  mapM tellMonster (monsters r)
  putStrLn ""
  return ()
