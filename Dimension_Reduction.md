# Dimensionality Reduction Methods Explained

When you have many features (like swell height, wind speed, tide levels, derived stats, etc.), it becomes hard to visualize the data.
Dimensionality reduction algorithms compress your high-dimensional dataset into 2D (or 3D) while trying to keep the structure of the data intact.

The three most common methods — PCA, t-SNE, and UMAP — do this in different ways.

## 1. PCA — Principal Component Analysis
### What PCA does

- PCA is a linear dimensionality reduction technique.

- It finds directions (called principal components) that capture the maximum variance in the data.

- The first component explains the most variability, the second explains the next most, and so on.

### How it works (conceptually)

- Imagine rotating your multi-dimensional data cloud.

- PCA finds the "best" orientation so that the first axis captures as much structure as possible.

- Then it projects the data onto the top few axes (e.g., 2 axes for a plot).

### Strengths

- Very fast

- Easy to interpret numerically

- Good for linear relationships

- Useful for identifying which variables drive the variance

### Weaknesses

- Cannot capture non-linear structure

- Often clusters look smeared or overlapping

## When to use PCA

- As a first visualization attempt

- When you want interpretability (“Which features matter most?”)

- When the data is mostly linear

## 2. t-SNE — t-Distributed Stochastic Neighbor Embedding
### What t-SNE does

- t-SNE focuses on preserving local structure — i.e., it tries to keep nearby points close together in 2D space.

### How it works (conceptually)

- Creates probability distributions of pairwise similarities in high-dimensional space.

- Then tries to create a 2D embedding where those pairwise similarities are preserved.

- It exaggerates the separation of clusters so that groups appear very distinct.

### Strengths

- Excellent at uncovering cluster structure

- Great for separating clearly different classes

- Good for messy, nonlinear data (which your surf data resembles)

### Weaknesses

- Slow on large datasets

- Does not preserve global structure (distances between clusters are not meaningful)

- Different runs may produce different layouts

- Harder to interpret quantitatively

### When to use t-SNE

- When you want to see clusters

- When data is highly non-linear

- When interpretability is NOT the priority

- When you're mainly concerned with visual structure, not distances

## 3. UMAP — Uniform Manifold Approximation and Projection
### What UMAP does

- UMAP is similar to t-SNE but:

    - Faster

    - Preserves more global structure

    - Better suited for large datasets

    - More stable

### How it works (conceptually)

- Assumes your data lies on an underlying manifold

- Computes a graph representing local neighborhoods

- Optimizes a low-dimensional layout that preserves both:

- local relationships (like t-SNE)

- some global geometry (better than t-SNE)

### Strengths

- Very fast

- Excellent clustering performance

- More faithful to real distances between clusters

- Less reliant on tuning parameters

### Weaknesses

- Less interpretable than PCA

- Still not perfectly preserving global scale

### When to use UMAP

When you want t-SNE-like cluster separation but:

- faster speed

- better global structure

- more reproducible results