import React from 'react';
import './Posts.scss';
import Post from '../Post/Post.js'
import data from '../../data.js';

const Posts = () => {
    return (
        <div className="posts">
            {data.map((item) => (
                <Post item={item} key={item.key} />
            ))}
        </div>
    )
}

export default Posts;