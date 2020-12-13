<!-- bootstrap navbar, will load options relative to user login state -->
<nav class="navbar navbar-expand-lg navbar-light bg-light">
  <a class="navbar-brand" href="#">Awesome Microblog</a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>
  <?php if (isset($this->session->username)) { ?>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav">
        <li class="nav-item active">
          <a class="nav-link" href="<?php $un=$this->session->username; echo "http://" . site_url("user/view/$un"); ?>"> Homepage <span class="sr-only">(current)</span></a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="<?php $un=$this->session->username; echo "http://" . site_url("user/feed/$un"); ?>"> feed </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="<?php echo "http://" . site_url("search"); ?>">search</a>

        </li>
        <li class="nav-item">
          <a class="nav-link" href="<?php echo "http://" . site_url("user/logout"); ?>">logout</a>

        </li>
      </ul>
    </div>
  <?php } else { ?>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav">
        <li class="nav-item active">
          <a class="nav-link" href="<?php echo "http://" . site_url("user/login"); ?>"> Login <span class="sr-only">(current)</span></a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="<?php echo "http://" . site_url("search"); ?>">search</a>

        </li>
      </ul>
    </div>
  <?php } ?>
</nav>
