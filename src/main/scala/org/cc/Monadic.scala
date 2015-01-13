package main.scala.org.cc

import scala.language.higherKinds

object Monadic {
  trait Monad[M[_]] {
    def unit[A](a : A) : M[A]
    def join[A](m : M[M[A]]) : M[A]
    def fmap[A,B](m : M[A], f : A => B) : M[B]

    def bind[A,B](m : M[A], f : A => M[B]) : M[B] = join(fmap(m,f))



    def sequence[A](l : List[M[A]]) : M[List[A]] = l match {
      case Nil    => unit[List[A]](Nil)
      case t :: q => bind(t          , (x : A      ) =>
                     bind(sequence(q), (r : List[A]) =>
                     unit(x :: r) ))
    }
  }

  trait Monadic[M[_],A] extends Monad[M] {
    def self : M[A]

    def map[B](f : A => B) : M[B] = fmap(self,f)
    def >>=[B](f: A => M[B]) : M[B] = bind(self,f)
    def >>[B](m : M[B]) = >>=(_ => m)
  }

}



