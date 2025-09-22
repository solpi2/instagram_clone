import { Link } from "react-router-dom";
import Avatar from "../common/Avatar";

const PostCard = ({ post }) => {
  return (
    <div className="bg-white rounded-lg shadow-md">
      <Link
        to={`/profile/${post.user.username}`}
        className="flex items-center space-x-3"
      >
        <Avatar user={post.user} size="medium" />
        <div>
          <p className="font-semibold text-sm">{post.user.username}</p>
        </div>
      </Link>
    </div>
  );
};

export default PostCard;