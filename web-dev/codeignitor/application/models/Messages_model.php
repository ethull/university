<?php
//messages model, sql commands related to user messages
class Messages_model extends CI_Model {
public function __construct() { $this->load->database(); }
    //select message details via username and order by most recent
    public function getMessagesByPoster($string) {
        $sql = 'SELECT user_username, text, posted_at FROM Messages WHERE user_username = ? ORDER BY posted_at DESC';
        $query = $this->db->query($sql, array($string));
        return $query->result_array();    }
    public function searchMessages($string) {
        //query builder used instead of query() to make LIKE wildcards work
        //$sql = 'SELECT user_username, text, posted_at FROM Messages WHERE text LIKE %?%';
        $query = $this->db->select('user_username, text, posted_at')->like('text', $string, 'both')->get('Messages');
        return $query->result_array();
    }
    public function insertMessage($poster, $string) {
        $currentDate = date("Y-m-d h:i:s");
        //pass null to auto increment id col
        $sql = 'INSERT INTO Messages VALUES (?, ?, ?, null)';
        $this->db->query($sql, array($poster, $string, $currentDate));
        //echo "insert result: $query";
    }
    public function getFollowedMessages($name) {
        //select who $name follows and then use results to select correct message vis username
        $sql = 'SELECT user_username, text, posted_at FROM Messages
        WHERE user_username IN (SELECT followed_username FROM User_Follows WHERE follower_username = ?) ORDER BY posted_at DESC';
        //alternative join syntax
        //$sql = 'SELECT SELECT follower_username, text, posted_at FROM Messages m join User_Follows u on u.followed_username = m.user_username WHERE u.follower_username = ? ORDER BY posted_at DESC';
        $query = $this->db->query($sql, array($name));
        return $query->result_array();
    }
}
?>
