<?php
//users model, sql commands related to user accounts and relationships
class Users_model extends CI_Model {
public function __construct() { $this->load->database(); }
    public function checkLogin($username, $pass) {
        $sql = 'SELECT username, password FROM Users WHERE username = ? AND password = ?';
        //hash pw
        $query = $this->db->query($sql, array($username, sha1($pass)));
        $length = count($query->result_array());
        //if a user matches their will be 1 result returned
        if ($length > 0) return true;
        else return false;
    }

    public function isFollowing($follower, $followed) {
        $sql = 'SELECT followed_username FROM User_Follows WHERE follower_username = ? AND followed_username = ?';
        $query = $this->db->query($sql, array($follower, $followed));
        $length = count($query->result_array());
        if ($length > 0) return true;
        else return false;
    }

    public function follow($follower, $followed) {
        //note $follower and $followed must be present in Users to fit foriegn key requirement
        $sql = 'INSERT INTO User_Follows VALUES (?, ?)';
        $query = $this->db->query($sql, array($follower, $followed));
    }
}
?>
